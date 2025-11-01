import { useParams, Link } from 'react-router-dom';
import { useState } from 'react';
import { FiArrowLeft, FiImage, FiMic, FiVideo } from 'react-icons/fi';
import ImageUpload from '../components/media/ImageUpload';
import TTSGenerator from '../components/tts/TTSGenerator';
import VideoGenerator from '../components/video/VideoGenerator';

const FolderDetailPage = () => {
  const { categoryId, folderName } = useParams();
  const [activeTab, setActiveTab] = useState('images');

  const tabs = [
    { id: 'images', label: 'Images', icon: FiImage },
    { id: 'tts', label: 'Text-to-Speech', icon: FiMic },
    { id: 'video', label: 'Generate Video', icon: FiVideo },
  ];

  return (
    <div className="p-6">
      <div className="mb-6">
        <Link
          to={`/categories/${categoryId}`}
          className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4"
        >
          <FiArrowLeft />
          Back to Category
        </Link>
        <h1 className="text-3xl font-bold text-gray-900">{folderName}</h1>
        <p className="text-gray-600 mt-1">Create your presentation</p>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 mb-6">
        <div className="border-b border-gray-200">
          <nav className="flex">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-2 px-6 py-4 font-medium transition ${
                    activeTab === tab.id
                      ? 'text-blue-600 border-b-2 border-blue-600'
                      : 'text-gray-600 hover:text-gray-900'
                  }`}
                >
                  <Icon />
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </div>
        <div className="p-6">
          {activeTab === 'images' && (
            <ImageUpload categoryId={categoryId} folderName={folderName} />
          )}
          {activeTab === 'tts' && (
            <TTSGenerator categoryId={categoryId} folderName={folderName} />
          )}
          {activeTab === 'video' && (
            <VideoGenerator categoryId={categoryId} folderName={folderName} />
          )}
        </div>
      </div>
    </div>
  );
};

export default FolderDetailPage;

