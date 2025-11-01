import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { categoryService } from '../services/api/categoryService';
import CreateFolderModal from '../components/folder/CreateFolderModal';
import { useState } from 'react';
import { FiPlus, FiArrowLeft, FiFolder } from 'react-icons/fi';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

const CategoryDetailPage = () => {
  const { categoryId } = useParams();
  const [showCreateFolder, setShowCreateFolder] = useState(false);

  const { data: category, isLoading, error, refetch } = useQuery({
    queryKey: ['category', categoryId],
    queryFn: async () => {
      const response = await categoryService.getCategoryById(categoryId);
      return response.data;
    },
    enabled: !!categoryId,
  });

  if (isLoading) {
    return <LoadingSpinner fullScreen />;
  }

  if (error) {
    return <ErrorMessage message="Failed to load category" onRetry={refetch} />;
  }

  return (
    <div className="p-6">
      <div className="mb-6">
        <Link
          to="/categories"
          className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4"
        >
          <FiArrowLeft />
          Back to Categories
        </Link>
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{category?.name}</h1>
            <p className="text-gray-600 mt-1">Manage folders in this category</p>
          </div>
          <button
            onClick={() => setShowCreateFolder(true)}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            <FiPlus />
            Create Folder
          </button>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <p className="text-gray-500 text-center py-12">
          Folders will be displayed here. Click "Create Folder" to get started.
        </p>
      </div>

      <CreateFolderModal
        isOpen={showCreateFolder}
        onClose={() => setShowCreateFolder(false)}
        categoryId={categoryId}
      />
    </div>
  );
};

export default CategoryDetailPage;

