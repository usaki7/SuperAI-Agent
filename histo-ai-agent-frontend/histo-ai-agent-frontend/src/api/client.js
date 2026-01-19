import axios from "axios";

export const API_BASE_URL = "http://localhost:8123/api";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000
});

export const buildSseUrl = (path, params) => apiClient.getUri({ url: path, params });
