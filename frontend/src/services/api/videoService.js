import httpClient from '../httpClient';

export const videoService = {
  // POST /allCategories/{categoryId}/folders/{folderName}/generateVideo
  generateVideo: (categoryId, folderName) => {
    return httpClient.post(
      `/allCategories/${categoryId}/folders/${folderName}/generateVideo`
    );
  },

  // POST /convert_videos
  mergeVideos: () => {
    return httpClient.post('/convert_videos');
  },

  // POST /finish
  finish: () => {
    return httpClient.post('/finish');
  },
};

