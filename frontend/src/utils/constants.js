export const API_BASE_URL = '/api/catalog';

export const ROUTES = {
  DASHBOARD: '/',
  CATEGORIES: '/categories',
  CATEGORY_DETAIL: '/categories/:categoryId',
  FOLDER_DETAIL: '/categories/:categoryId/folders/:folderName',
  MEDIA_LIBRARY: '/media',
  VIDEO_STUDIO: '/studio',
};

export const VIDEO_EXTENSIONS = ['.mp4', '.avi', '.mkv'];
export const IMAGE_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.gif'];

export const MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

