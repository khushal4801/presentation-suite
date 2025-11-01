import httpClient from '../httpClient';

export const ttsService = {
  // POST /allCategories/{categoryId}/folders/{folderName}/tts
  generateTTS: (categoryId, folderName, text) => {
    return httpClient.post(
      `/allCategories/${categoryId}/folders/${folderName}/tts`,
      { text }
    );
  },
};

