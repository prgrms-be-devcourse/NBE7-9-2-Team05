import { fetchApi } from "./client";

type ResponseData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

export type UserProfile = {
  userId: number;
  email: string;
  nickname: string;
  userProfile: string | null;
  userProfileUrl: string | null;
};

type UpdateUserProfilePayload = {
  nickname?: string;
  userProfile?: string;
};

const USER_PATH = "/api/v1/users";

class UserService {
  async getProfile(): Promise<UserProfile> {
    const response: ResponseData<UserProfile> = await fetchApi(
      `${USER_PATH}/profile`,
      {
        method: "GET",
        credentials: "include",
      },
    );

    return response.data;
  }

  async updateProfile(
    payload: UpdateUserProfilePayload,
  ): Promise<UserProfile> {
    const response: ResponseData<UserProfile> = await fetchApi(
      `${USER_PATH}/profile`,
      {
        method: "PUT",
        credentials: "include",
        body: JSON.stringify(payload),
      },
    );

    return response.data;
  }
}

export const userService = new UserService();
