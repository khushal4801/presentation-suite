import { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { FiUpload, FiX, FiVideo } from 'react-icons/fi';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { mediaService } from '../../services/api/mediaService';
import { isValidVideoFile } from '../../utils/helpers';
import toast from 'react-hot-toast';

const VideoUpload = ({ onUploadComplete }) => {
  const [files, setFiles] = useState([]);
  const queryClient = useQueryClient();

  const uploadMutation = useMutation({
    mutationFn: (filesToUpload) => mediaService.uploadVideos(filesToUpload),
    onSuccess: () => {
      toast.success('Videos uploaded successfully');
      setFiles([]);
      queryClient.invalidateQueries(['videos']);
      if (onUploadComplete) onUploadComplete();
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to upload videos');
    },
  });

  const onDrop = useCallback((acceptedFiles) => {
    const validFiles = acceptedFiles.filter(isValidVideoFile);
    if (validFiles.length !== acceptedFiles.length) {
      toast.error('Only MP4 files are allowed');
    }
    setFiles((prev) => [...prev, ...validFiles]);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'video/mp4': ['.mp4'],
    },
  });

  const removeFile = (index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleUpload = () => {
    if (files.length === 0) {
      toast.error('Please select at least one video');
      return;
    }
    uploadMutation.mutate(files);
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Upload Videos</h3>
      
      <div
        {...getRootProps()}
        className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition ${
          isDragActive
            ? 'border-blue-500 bg-blue-50'
            : 'border-gray-300 hover:border-gray-400'
        }`}
      >
        <input {...getInputProps()} />
        <FiVideo className="text-4xl text-gray-400 mx-auto mb-4" />
        {isDragActive ? (
          <p className="text-blue-600">Drop the videos here...</p>
        ) : (
          <div>
            <p className="text-gray-600 mb-2">
              Drag & drop videos here, or click to select
            </p>
            <p className="text-sm text-gray-500">
              Supports: MP4 only
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
          <div className="space-y-2">
            {files.map((file, index) => (
              <div key={index} className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                <FiVideo className="text-gray-600" />
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-900">{file.name}</p>
                  <p className="text-xs text-gray-500">
                    {(file.size / 1024 / 1024).toFixed(2)} MB
                  </p>
                </div>
                <button
                  onClick={() => removeFile(index)}
                  className="text-red-600 hover:text-red-800"
                >
                  <FiX />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default VideoUpload;

