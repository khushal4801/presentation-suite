export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
};

export const getFileExtension = (filename) => {
  return filename.slice((filename.lastIndexOf('.') - 1 >>> 0) + 2);
};

export const isValidImageFile = (file) => {
  const validExtensions = ['.jpg', '.jpeg', '.png', '.gif'];
  const extension = getFileExtension(file.name).toLowerCase();
  return validExtensions.includes('.' + extension);
};

export const isValidVideoFile = (file) => {
  const validExtensions = ['.mp4'];
  const extension = getFileExtension(file.name).toLowerCase();
  return validExtensions.includes('.' + extension);
};

export const generateImageNumber = (index) => {
  return String(index + 1).padStart(3, '0');
};

