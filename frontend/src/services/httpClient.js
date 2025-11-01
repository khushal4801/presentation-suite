import axios from 'axios';
import toast from 'react-hot-toast';

const API_BASE_URL = '/api/catalog';

const httpClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
httpClient.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
httpClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const message = error.response?.data?.message || error.message || 'An error occurred';
    toast.error(message);
    return Promise.reject(error);
  }
);

export default httpClient;

