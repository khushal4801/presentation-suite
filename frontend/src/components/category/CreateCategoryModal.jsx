import { useState } from 'react';
import { FiX } from 'react-icons/fi';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { categoryService } from '../../services/api/categoryService';
import toast from 'react-hot-toast';

const CreateCategoryModal = ({ isOpen, onClose }) => {
  const [categoryName, setCategoryName] = useState('');
  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: (data) => categoryService.createCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries(['categories']);
      toast.success('Category created successfully');
      setCategoryName('');
      onClose();
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Failed to create category');
    },
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!categoryName.trim()) {
      toast.error('Category name is required');
      return;
    }
    createMutation.mutate({ name: categoryName.trim() });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-xl font-semibold text-gray-900">Create Category</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition"
          >
            <FiX className="text-xl" />
          </button>
        </div>
        <form onSubmit={handleSubmit} className="p-6">
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Category Name
            </label>
            <input
              type="text"
              value={categoryName}
              onChange={(e) => setCategoryName(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter category name"
              autoFocus
            />
          </div>
          <div className="flex justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={createMutation.isPending}
              className="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
            >
              {createMutation.isPending ? 'Creating...' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateCategoryModal;

