/* eslint-disable @typescript-eslint/no-explicit-any */
'use client';

import React, { createContext, useContext, useEffect, useMemo, useRef } from "react";
import { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import SocketJS from 'sockjs-client';

type AnyPayload = Record<string, any>;
type Listener<T = AnyPayload> = (payload: T) => void;

interface StompContextValue {
    subscribeTo: <T = AnyPayload>(topic: string, fn:Listener<T>) => () => void;
    send: (destination: string, body: any) => void;
}

const StompContext = createContext<StompContextValue | null>(null);

export function StompProvider({ children }: { children: React.ReactNode }) {
    const clientRef = useRef<Client | null>(null);
    const topicListenerRef = useRef<Map<string, Set<Listener>>>(new Map());
    const activeSubRef = useRef<Map<string, StompSubscription>>(new Map());
    const tokenRef = useRef<string | null>(null);

    useEffect(() => {
        tokenRef.current = getAccessTokenFromCookie();
        const socketFactory = () => new SocketJS(`${process.env.NEXT_PUBLIC_API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: socketFactory,
            connectHeaders: tokenRef.current ? { Authorization: `Bearer ${tokenRef.current}` } : {},
            reconnectDelay: 5000,
            heartbeatIncoming: 20000,
            heartbeatOutgoing: 20000,
            debug: (message) => console.log('[STOMP]', message),
            onConnect: () => {
                console.log('[STOMP] Connected');

                for (const topic of topicListenerRef.current.keys()) {
                    ensureSubscribed(topic);
                }
            },
            onDisconnect: () => console.log('[STOMP] Disconnected'),
            onStompError: (frame) => console.error('[STOMP Error]', frame.headers['message']),
        });

        client.activate();
        clientRef.current = client;

        return () => {
            client.deactivate();
            clientRef.current = null;
            activeSubRef.current.clear();
            topicListenerRef.current.clear();
        };
    }, []);

    const ensureSubscribed = (topic: string) => {
        const client = clientRef.current;

        if (!client?.connected) {
            return;
        }

        if(activeSubRef.current.has(topic)) {
            return;
        }

        const sub = client.subscribe(topic, (message: IMessage) => {
            const payload = safeParse(message.body);

            if(!payload) {
                return;
            }

            const listeners = topicListenerRef.current.get(topic);

            if (!listeners || listeners.size === 0) {
                return;
            }

            for (const fn of listeners) {
                try {
                    fn(payload);
                } catch (e) {
                    console.error(`[STOMP] listener error ${topic}`, e);
                }
            }
        });
        
        activeSubRef.current.set(topic, sub);
    }

    const subscribeTo = <T,>(topic: string, fn: Listener<T>) => {
        let set = topicListenerRef.current.get(topic);
        
        if (!set) {
            set = new Set();
            topicListenerRef.current.set(topic, set);
            ensureSubscribed(topic);
        }
        
        set.add(fn as Listener);

        return () => {
            const cur = topicListenerRef.current.get(topic);

            if (!cur) {
                return;
            }

            cur.delete(fn as Listener);

            if (cur.size === 0) {
                const sub = activeSubRef.current.get(topic);

                if (sub) {
                    try {
                        sub.unsubscribe();
                    } catch {}

                    activeSubRef.current.delete(topic);
                }

                topicListenerRef.current.delete(topic);
            }
        };
    };

    const send = (destination: string, body: any) => {
        const client = clientRef.current;
        if (client?.connected) {
            client.publish({ destination, body: JSON.stringify(body) });
        }
    };

    const value = useMemo(() => ({ subscribeTo, send }), []);

    return (
        <StompContext.Provider value={value}>
            {children}
        </StompContext.Provider>
    )
}

function safeParse<T = AnyPayload>(raw: string): T | null {
    try {
        return JSON.parse(raw) as T;
    } catch {
        return null;
    }
  }

function getAccessTokenFromCookie(): string | null {
    if (typeof document === 'undefined') {
        return null;
    }

    const cookieString = document.cookie;
    const cookies = Object.fromEntries(
        cookieString.split('; ').map((c) => c.split('='))
    );

    return cookies['accessToken'] ?? null;
}

export function useStomp() {
    const context = useContext(StompContext);
    if (!context) {
        throw new Error('useStomp must be used within StompProvider');
    }

    return context;
}
