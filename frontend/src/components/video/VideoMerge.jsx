import { useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { videoService } from '../../services/api/videoService';
import { mediaService } from '../../services/api/mediaService';
import { FiVideo, FiMerge, FiTrash2 } from 'react-icons/fi';
import LoadingSpinner from '../common/LoadingSpinner';
import toast from 'react-hot-toast';
import ConfirmDialog from '../common/ConfirmDialog';

const VideoMerge = () => {
  const [showFinishDialog, setShowFinishDialog] = useState(false);

  const { data: videos, isLoading } = useQuery({
    queryKey: ['videos'],
    queryFn: async () => {
      const response = await mediaService.listVideos();
      return response.data;
    },
  });

  const mergeMutation = useMutation({
    mutationFn: () => videoService.mergeVideos(),
    onSuccess: (response) => {
      toast.success('Videos merged successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to merge videos');
    },
  });

  const finishMutation = useMutation({
    mutationFn: () => videoService.finish(),
    onSuccess: () => {
      toast.success('Upload folder cleared');
      setShowFinishDialog(false);
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to clear folder');
    },
  });

  const handleMerge = () => {
    if (!videos || videos.length === 0) {
      toast.error('No videos to merge');
      return;
    }
    mergeMutation.mutate();
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Video Studio</h2>
          <p className="text-gray-600 mt-1">Merge videos with background music</p>
        </div>
        <button
          onClick={() => setShowFinishDialog(true)}
          className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
        >
          <FiTrash2 className="inline mr-2" />
          Clear All
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex items-center gap-3 mb-4">
          <div className="bg-blue-100 p-3 rounded-lg">
            <FiVideo className="text-blue-600 text-2xl" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Videos to Merge</h3>
            <p className="text-sm text-gray-500">
              {videos?.length || 0} video(s) ready for merging
            </p>
          </div>
        </div>

        {videos && videos.length > 0 ? (
          <div className="mb-6">
            <div className="space-y-2">
              {videos.map((video, index) => (
                <div
                  key={video.name}
                  className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg"
                >
                  <span className="text-sm font-medium text-gray-600 w-8">
                    {index + 1}.
                  </span>
                  <span className="flex-1 text-sm text-gray-900">{video.name}</span>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            <p>No videos found. Upload videos first.</p>
          </div>
        )}

        <button
          onClick={handleMerge}
          disabled={mergeMutation.isPending || !videos || videos.length === 0}
          className="w-full px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          <FiMerge />
          {mergeMutation.isPending ? 'Merging Videos...' : 'Merge Videos'}
        </button>

        {mergeMutation.isSuccess && mergeMutation.data && (
          <div className="mt-4 p-4 bg-green-50 rounded-lg border border-green-200">
            <p className="text-sm text-green-800">
              {mergeMutation.data.data?.message || 'Videos merged successfully'}
            </p>
            {mergeMutation.data.data?.outputFilePath && (
              <p className="text-xs text-green-600 mt-1">
                Output: {mergeMutation.data.data.outputFilePath}
              </p>
            )}
          </div>
        )}
      </div>

      <ConfirmDialog
        isOpen={showFinishDialog}
        title="Clear Upload Folder"
        message="Are you sure you want to delete all videos in the upload folder? This action cannot be undone."
        onConfirm={() => finishMutation.mutate()}
        onCancel={() => setShowFinishDialog(false)}
        confirmText="Clear"
      />
    </div>
  );
};

export default VideoMerge;

