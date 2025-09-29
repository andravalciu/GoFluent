import { Injectable } from '@angular/core';
import axios, { AxiosRequestConfig } from 'axios';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  constructor() {
    // baza pentru toate requesturile
    axios.defaults.baseURL = "http://localhost:8080";
    axios.defaults.headers.post["Content-Type"] = "application/json";

    // Interceptor pentru token
    axios.interceptors.request.use(config => {
      const token = this.getAuthToken();
      const isPublicEndpoint =
        config.url?.includes("/login") || config.url?.includes("/register");

      if (token && !isPublicEndpoint) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`;
        console.log("✅ Token trimis:", config.headers.Authorization.substring(0, 20) + "...");
      } else {
        console.log("ℹ️ Public endpoint sau lipsă token.");
      }

      return config;
    });
  }

  // ==============================
  // TOKEN MANAGEMENT
  // ==============================
  getAuthToken(): string | null {
    return window.localStorage.getItem("auth_token");
  }

  setAuthToken(token: string | null): void {
    if (token) {
      window.localStorage.setItem("auth_token", token);
    } else {
      window.localStorage.removeItem("auth_token");
    }
  }

  // ==============================
  // GENERIC REQUEST
  // ==============================
  request(method: string, url: string, data?: any): Promise<any> {
    const config: AxiosRequestConfig = {
      method,
      url
    };

    if (method.toLowerCase() === "get") {
      config.params = data;
    } else if (data) {
      config.data = data;
    }

    console.log("📤 Request:", method, url, data || "");
    return axios(config);
  }
}
