import { fetchApi } from "./client";

type ResponseData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type UploadUrlResponse = {
  objectKey: string;
  uploadUrl: string;
};

type CreateUploadUrlPayload = {
  originalFileName: string;
  contentType: string;
  objectKey?: string;
};

const STORAGE_PATH = "/api/v1/storage";

class StorageService {
  async createUploadUrl(
    payload: CreateUploadUrlPayload,
  ): Promise<UploadUrlResponse> {
    const response: ResponseData<UploadUrlResponse> = await fetchApi(
      `${STORAGE_PATH}/upload-url`,
      {
        method: "POST",
        credentials: "include",
        body: JSON.stringify(payload),
      },
    );

    return response.data;
  }
}

export const storageService = new StorageService();
