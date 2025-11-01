import { useQuery } from '@tanstack/react-query';
import { categoryService } from '../services/api/categoryService';
import { Link } from 'react-router-dom';
import { FiFolder, FiPlus } from 'react-icons/fi';
import LoadingSpinner from '../components/common/LoadingSpinner';

const Dashboard = () => {
  const { data: categories, isLoading } = useQuery({
    queryKey: ['categories'],
    queryFn: async () => {
      const response = await categoryService.getAllCategories();
      return response.data;
    },
  });

  if (isLoading) {
    return <LoadingSpinner fullScreen />;
  }

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-2">Welcome to Presentation Suite</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Categories</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">
                {categories?.length || 0}
              </p>
            </div>
            <FiFolder className="text-4xl text-blue-600" />
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-900">Recent Categories</h2>
          <Link
            to="/categories"
            className="text-blue-600 hover:text-blue-700 font-medium"
          >
            View All
          </Link>
        </div>

        {categories && categories.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {categories.slice(0, 6).map((category) => (
              <Link
                key={category.id}
                to={`/categories/${category.id}`}
                className="p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:shadow-md transition"
              >
                <div className="flex items-center gap-3">
                  <FiFolder className="text-blue-600 text-xl" />
                  <span className="font-medium text-gray-900">{category.name}</span>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <FiFolder className="text-6xl text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500 mb-4">No categories yet</p>
            <Link
              to="/categories"
              className="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
            >
              <FiPlus />
              Create Category
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;

