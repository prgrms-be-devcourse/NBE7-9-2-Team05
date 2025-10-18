/* eslint-disable @typescript-eslint/no-explicit-any */

import { fetchApi } from "./client";

const AUTH_PATH = "/api/v1/auth/local";

type LoginPayload = {
  email: string;
  password: string;
};

type SignupPayload = {
  email: string;
  nickname: string;
  password: string;
  passwordConfirm?: string;
};

class AuthService {
  async login(payload: LoginPayload): Promise<any> {
    return fetchApi(`${AUTH_PATH}/login`, {
      method: "POST",
      credentials: "include",
      body: JSON.stringify(payload),
    });
  }

  async signup(payload: SignupPayload): Promise<any> {
    const { passwordConfirm, ...signupData } = payload;

    return fetchApi(`${AUTH_PATH}/signup`, {
      method: "POST",
      credentials: "include",
      body: JSON.stringify(signupData),
    });
  }
}

export const authService = new AuthService();
