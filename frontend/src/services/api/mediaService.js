import httpClient from '../httpClient';

export const mediaService = {
  // POST /allCategories/{categoryId}/folders/{folderName}/upload
  uploadImages: (categoryId, folderName, files) => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append('files', file);
    });
    return httpClient.post(
      `/allCategories/${categoryId}/folders/${folderName}/upload`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
  },

  // POST /uploadVideos
  uploadVideos: (files) => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append('video', file);
    });
    return httpClient.post('/uploadVideos', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  // GET /videos
  listVideos: () => {
    return httpClient.get('/videos');
  },

  // DELETE /videos/{videoName}
  deleteVideo: (videoName) => {
    return httpClient.delete(`/videos/${videoName}`);
  },
};

