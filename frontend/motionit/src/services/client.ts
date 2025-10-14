export const fetchApi = (url: string, options?: RequestInit) => {
    if (options?.body) {
        const headers = new Headers(options.headers || {});
        headers.set("Content-Type", "application/json");
        options.headers = headers;
    } 

    return fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}${url}`, options).then( async (res) => {
        if(!res.ok) {
            const resultData = await res.json();
            throw new Error(resultData.msg || "Failed Request");
        }

        return res.json();
    });
}