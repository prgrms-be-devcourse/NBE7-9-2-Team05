/* eslint-disable @typescript-eslint/no-explicit-any */

import { fetchApi } from "./client";

const PATH = '/home'

class ExampleService {
    async getExample(): Promise<any> {
        const response = await fetchApi(PATH);
        
        return response;
    }
}

export const exampleService = new ExampleService();
