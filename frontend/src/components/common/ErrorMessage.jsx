import { FiAlertCircle } from 'react-icons/fi';

const ErrorMessage = ({ message, onRetry, onDismiss }) => {
  return (
    <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-start gap-3">
      <FiAlertCircle className="text-red-600 text-xl flex-shrink-0 mt-0.5" />
      <div className="flex-1">
        <p className="text-red-800 font-medium">{message}</p>
        {onRetry && (
          <button
            onClick={onRetry}
            className="mt-2 text-red-600 hover:text-red-800 underline text-sm"
          >
            Retry
          </button>
        )}
      </div>
      {onDismiss && (
        <button
          onClick={onDismiss}
          className="text-red-600 hover:text-red-800"
        >
          ×
        </button>
      )}
    </div>
  );
};

export default ErrorMessage;

