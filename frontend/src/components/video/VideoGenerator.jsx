import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { videoService } from '../../services/api/videoService';
import { FiVideo, FiCheckCircle, FiAlertCircle } from 'react-icons/fi';
import toast from 'react-hot-toast';

const VideoGenerator = ({ categoryId, folderName, onGenerateComplete }) => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [generatedVideo, setGeneratedVideo] = useState(null);

  const generateMutation = useMutation({
    mutationFn: () => videoService.generateVideo(categoryId, folderName),
    onSuccess: (response) => {
      toast.success('Video generated successfully');
      setGeneratedVideo(response.data);
      setIsGenerating(false);
      if (onGenerateComplete) onGenerateComplete();
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to generate video');
      setIsGenerating(false);
    },
  });

  const handleGenerate = () => {
    setIsGenerating(true);
    generateMutation.mutate();
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center gap-3 mb-6">
        <div className="bg-green-100 p-3 rounded-lg">
          <FiVideo className="text-green-600 text-2xl" />
        </div>
        <div>
          <h3 className="text-lg font-semibold text-gray-900">Generate Video</h3>
          <p className="text-sm text-gray-500">Create video from images and audio</p>
        </div>
      </div>

      <div className="mb-6 space-y-3">
        <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
          <FiCheckCircle className="text-green-600" />
          <span className="text-sm text-gray-700">Images uploaded</span>
        </div>
        <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
          <FiCheckCircle className="text-green-600" />
          <span className="text-sm text-gray-700">Audio generated</span>
        </div>
      </div>

      {isGenerating ? (
        <div className="text-center py-8">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent mb-4"></div>
          <p className="text-gray-600">Generating video... This may take a few moments.</p>
        </div>
      ) : (
        <button
          onClick={handleGenerate}
          disabled={generateMutation.isPending}
          className="w-full px-4 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          <FiVideo />
          {generateMutation.isPending ? 'Generating...' : 'Generate Video'}
        </button>
      )}

      {generatedVideo && (
        <div className="mt-6 p-4 bg-green-50 rounded-lg border border-green-200">
          <div className="flex items-center gap-2 mb-2">
            <FiCheckCircle className="text-green-600" />
            <span className="text-sm font-medium text-green-900">Video Generated Successfully</span>
          </div>
          <p className="text-sm text-green-700">{generatedVideo.message || generatedVideo.outputPath}</p>
        </div>
      )}
    </div>
  );
};

export default VideoGenerator;

