import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { mediaService } from '../../services/api/mediaService';
import LoadingSpinner from '../common/LoadingSpinner';
import ErrorMessage from '../common/ErrorMessage';
import ReactPlayer from 'react-player';
import { FiTrash2, FiPlay } from 'react-icons/fi';
import { useState } from 'react';
import ConfirmDialog from '../common/ConfirmDialog';
import toast from 'react-hot-toast';

const VideoList = () => {
  const [playingVideo, setPlayingVideo] = useState(null);
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, videoName: null });
  const queryClient = useQueryClient();

  const { data: videos, isLoading, error, refetch } = useQuery({
    queryKey: ['videos'],
    queryFn: async () => {
      const response = await mediaService.listVideos();
      return response.data;
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (videoName) => mediaService.deleteVideo(videoName),
    onSuccess: () => {
      queryClient.invalidateQueries(['videos']);
      toast.success('Video deleted successfully');
      setDeleteDialog({ isOpen: false, videoName: null });
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to delete video');
    },
  });

  const handleDelete = (videoName) => {
    deleteMutation.mutate(videoName);
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorMessage message="Failed to load videos" onRetry={refetch} />;
  }

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Videos</h2>
      
      {!videos || videos.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
          <p className="text-gray-500">No videos found</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {videos.map((video) => (
            <div key={video.name} className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
              <div className="aspect-video bg-gray-100 relative">
                {playingVideo === video.name ? (
                  <ReactPlayer
                    url={video.path}
                    width="100%"
                    height="100%"
                    controls
                    playing
                  />
                ) : (
                  <div className="absolute inset-0 flex items-center justify-center">
                    <button
                      onClick={() => setPlayingVideo(video.name)}
                      className="bg-black bg-opacity-50 text-white rounded-full p-4 hover:bg-opacity-70 transition"
                    >
                      <FiPlay className="text-2xl" />
                    </button>
                  </div>
                )}
              </div>
              <div className="p-4">
                <h3 className="font-semibold text-gray-900 mb-2">{video.name}</h3>
                <div className="flex items-center justify-between">
                  <p className="text-sm text-gray-500">{video.path}</p>
                  <button
                    onClick={() => setDeleteDialog({ isOpen: true, videoName: video.name })}
                    className="text-red-600 hover:text-red-800"
                  >
                    <FiTrash2 />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        title="Delete Video"
        message={`Are you sure you want to delete "${deleteDialog.videoName}"?`}
        onConfirm={() => handleDelete(deleteDialog.videoName)}
        onCancel={() => setDeleteDialog({ isOpen: false, videoName: null })}
        confirmText="Delete"
      />
    </div>
  );
};

export default VideoList;

