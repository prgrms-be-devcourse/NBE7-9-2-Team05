import { create } from "zustand";
import { User } from "../type";

interface UserProps {
    user: User;
    
    setUser: (user: User) => void;
}

export const useUser = create<UserProps>((set, get) => ({
    user: { id: 0, nickname: "", email: "" },

    setUser: (user: User) => {
        set({ user: { id: user.id, nickname: user.nickname, email: user.email } });
    }
}))
