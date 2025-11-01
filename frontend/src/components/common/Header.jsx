import { Link, useLocation } from 'react-router-dom';
import { FiHome, FiVideo, FiFolder } from 'react-icons/fi';

const Header = () => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <header className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="flex items-center gap-2">
            <FiVideo className="text-blue-600 text-2xl" />
            <span className="text-xl font-bold text-gray-900">Presentation Suite</span>
          </Link>
          <nav className="flex items-center gap-6">
            <Link
              to="/"
              className={`flex items-center gap-2 px-3 py-2 rounded-lg transition ${
                isActive('/') ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <FiHome />
              <span>Dashboard</span>
            </Link>
            <Link
              to="/categories"
              className={`flex items-center gap-2 px-3 py-2 rounded-lg transition ${
                isActive('/categories') ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <FiFolder />
              <span>Categories</span>
            </Link>
            <Link
              to="/media"
              className={`flex items-center gap-2 px-3 py-2 rounded-lg transition ${
                isActive('/media') ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <FiVideo />
              <span>Media</span>
            </Link>
            <Link
              to="/studio"
              className={`flex items-center gap-2 px-3 py-2 rounded-lg transition ${
                isActive('/studio') ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <FiVideo />
              <span>Studio</span>
            </Link>
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;

