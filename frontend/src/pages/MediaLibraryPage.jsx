import VideoUpload from '../components/media/VideoUpload';
import VideoList from '../components/media/VideoList';

const MediaLibraryPage = () => {
  return (
    <div>
      <div className="p-6 border-b bg-white">
        <h1 className="text-2xl font-bold text-gray-900">Media Library</h1>
        <p className="text-gray-600 mt-1">Upload and manage your videos</p>
      </div>
      <div className="p-6">
        <div className="mb-6">
          <VideoUpload />
        </div>
        <VideoList />
      </div>
    </div>
  );
};

export default MediaLibraryPage;

