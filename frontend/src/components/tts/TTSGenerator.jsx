import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { ttsService } from '../../services/api/ttsService';
import { FiMic, FiPlay, FiDownload } from 'react-icons/fi';
import toast from 'react-hot-toast';
import ReactPlayer from 'react-player';

const TTSGenerator = ({ categoryId, folderName, onGenerateComplete }) => {
  const [text, setText] = useState('');
  const [audioUrl, setAudioUrl] = useState(null);

  const generateMutation = useMutation({
    mutationFn: (textToConvert) => ttsService.generateTTS(categoryId, folderName, textToConvert),
    onSuccess: (response) => {
      toast.success('TTS generated successfully');
      // Assuming the response contains audio file path or URL
      // You may need to adjust this based on your backend response
      setAudioUrl(`/api/catalog/public/images/${categoryId}/${folderName}/audio.mp3`);
      if (onGenerateComplete) onGenerateComplete();
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to generate TTS');
    },
  });

  const handleGenerate = () => {
    if (!text.trim()) {
      toast.error('Please enter some text');
      return;
    }
    generateMutation.mutate(text.trim());
  };

  const characterCount = text.length;
  const wordCount = text.trim() ? text.trim().split(/\s+/).length : 0;

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center gap-3 mb-6">
        <div className="bg-purple-100 p-3 rounded-lg">
          <FiMic className="text-purple-600 text-2xl" />
        </div>
        <div>
          <h3 className="text-lg font-semibold text-gray-900">Text-to-Speech</h3>
          <p className="text-sm text-gray-500">Convert your text to audio</p>
        </div>
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Enter Text
        </label>
        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          rows={8}
          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
          placeholder="Type or paste your text here..."
        />
        <div className="flex justify-between items-center mt-2 text-sm text-gray-500">
          <span>{wordCount} words</span>
          <span>{characterCount} characters</span>
        </div>
      </div>

      <button
        onClick={handleGenerate}
        disabled={generateMutation.isPending || !text.trim()}
        className="w-full px-4 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
      >
        <FiMic />
        {generateMutation.isPending ? 'Generating...' : 'Generate Audio'}
      </button>

      {audioUrl && (
        <div className="mt-6 p-4 bg-gray-50 rounded-lg">
          <h4 className="text-sm font-medium text-gray-700 mb-3">Generated Audio</h4>
          <ReactPlayer
            url={audioUrl}
            controls
            width="100%"
            height="50px"
            className="mb-3"
          />
          <a
            href={audioUrl}
            download="audio.mp3"
            className="inline-flex items-center gap-2 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
          >
            <FiDownload />
            Download Audio
          </a>
        </div>
      )}
    </div>
  );
};

export default TTSGenerator;

