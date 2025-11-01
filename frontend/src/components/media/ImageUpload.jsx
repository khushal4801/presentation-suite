import { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { FiUpload, FiX, FiImage } from 'react-icons/fi';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { mediaService } from '../../services/api/mediaService';
import { isValidImageFile } from '../../utils/helpers';
import toast from 'react-hot-toast';

const ImageUpload = ({ categoryId, folderName, onUploadComplete }) => {
  const [files, setFiles] = useState([]);
  const queryClient = useQueryClient();

  const uploadMutation = useMutation({
    mutationFn: (filesToUpload) => mediaService.uploadImages(categoryId, folderName, filesToUpload),
    onSuccess: () => {
      toast.success('Images uploaded successfully');
      setFiles([]);
      queryClient.invalidateQueries(['folder', categoryId, folderName]);
      if (onUploadComplete) onUploadComplete();
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to upload images');
    },
  });

  const onDrop = useCallback((acceptedFiles) => {
    const validFiles = acceptedFiles.filter(isValidImageFile);
    if (validFiles.length !== acceptedFiles.length) {
      toast.error('Some files are not valid images');
    }
    setFiles((prev) => [...prev, ...validFiles]);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/*': ['.jpg', '.jpeg', '.png', '.gif'],
    },
  });

  const removeFile = (index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleUpload = () => {
    if (files.length === 0) {
      toast.error('Please select at least one image');
      return;
    }
    uploadMutation.mutate(files);
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Upload Images</h3>
      
      <div
        {...getRootProps()}
        className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition ${
          isDragActive
            ? 'border-blue-500 bg-blue-50'
            : 'border-gray-300 hover:border-gray-400'
        }`}
      >
        <input {...getInputProps()} />
        <FiImage className="text-4xl text-gray-400 mx-auto mb-4" />
        {isDragActive ? (
          <p className="text-blue-600">Drop the images here...</p>
        ) : (
          <div>
            <p className="text-gray-600 mb-2">
              Drag & drop images here, or click to select
            </p>
            <p className="text-sm text-gray-500">
              Supports: JPG, PNG, GIF
            </p>
          </div>
        )}
      </div>

      {files.length > 0 && (
        <div className="mt-4">
          <div className="flex items-center justify-between mb-2">
            <p className="text-sm font-medium text-gray-700">
              {files.length} file(s) selected
            </p>
            <button
              onClick={handleUpload}
              disabled={uploadMutation.isPending}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition disabled:opacity-50 flex items-center gap-2"
            >
              <FiUpload />
              {uploadMutation.isPending ? 'Uploading...' : 'Upload'}
            </button>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {files.map((file, index) => (
              <div key={index} className="relative group">
                <img
                  src={URL.createObjectURL(file)}
                  alt={file.name}
                  className="w-full h-32 object-cover rounded-lg border border-gray-200"
                />
                <button
                  onClick={() => removeFile(index)}
                  className="absolute top-2 right-2 bg-red-600 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition"
                >
                  <FiX />
                </button>
                <p className="text-xs text-gray-600 mt-1 truncate">{file.name}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default ImageUpload;

