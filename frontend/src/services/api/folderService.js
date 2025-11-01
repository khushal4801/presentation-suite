import httpClient from '../httpClient';

export const folderService = {
  // POST /allCategories/{id}/folders
  createFolder: (categoryId, folderName) => {
    return httpClient.post(`/allCategories/${categoryId}/folders`, {
      name: folderName,
    });
  },
};

