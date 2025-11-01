import { Link } from 'react-router-dom';
import { FiFolder, FiMoreVertical, FiTrash2 } from 'react-icons/fi';
import { useState } from 'react';
import ConfirmDialog from '../common/ConfirmDialog';

const CategoryCard = ({ category, onDelete }) => {
  const [showMenu, setShowMenu] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const handleDelete = () => {
    onDelete(category.id);
    setShowDeleteDialog(false);
    setShowMenu(false);
  };

  return (
    <>
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition relative group">
        <Link to={`/categories/${category.id}`} className="block p-6">
          <div className="flex items-center gap-4">
            <div className="bg-blue-100 p-3 rounded-lg">
              <FiFolder className="text-blue-600 text-2xl" />
            </div>
            <div className="flex-1">
              <h3 className="text-lg font-semibold text-gray-900">{category.name}</h3>
              <p className="text-sm text-gray-500 mt-1">Category</p>
            </div>
          </div>
        </Link>
        <div className="absolute top-4 right-4">
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="p-2 hover:bg-gray-100 rounded-lg transition opacity-0 group-hover:opacity-100"
          >
            <FiMoreVertical className="text-gray-600" />
          </button>
          {showMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-10">
              <button
                onClick={() => {
                  setShowDeleteDialog(true);
                  setShowMenu(false);
                }}
                className="w-full flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition"
              >
                <FiTrash2 />
                <span>Delete</span>
              </button>
            </div>
          )}
        </div>
      </div>
      <ConfirmDialog
        isOpen={showDeleteDialog}
        title="Delete Category"
        message={`Are you sure you want to delete "${category.name}"? This action cannot be undone.`}
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteDialog(false)}
        confirmText="Delete"
      />
    </>
  );
};

export default CategoryCard;

