// In development, use your local backend URL
export const API_BASE_URL = 'http://192.168.0.81:8080';

export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/api/v1/users/login`,
  REGISTER: `${API_BASE_URL}/api/v1/users/register`,
  USER_INFO: `${API_BASE_URL}/api/v1/users/token`,
} as const; 