# Presentation Suite Frontend

React frontend application for the Presentation Suite microservices architecture.

## Features

- ✅ Category Management
- ✅ Folder Management
- ✅ Image Upload
- ✅ Text-to-Speech Generation
- ✅ Video Generation from Images + Audio
- ✅ Video Upload
- ✅ Video Merging with Background Music
- ✅ Media Library

## Tech Stack

- **React 18** - UI Library
- **React Router v6** - Routing
- **TanStack Query (React Query)** - Server State Management
- **Axios** - HTTP Client
- **React Dropzone** - File Uploads
- **React Player** - Video/Audio Playback
- **React Hot Toast** - Notifications
- **Tailwind CSS** - Styling
- **Vite** - Build Tool

## Getting Started

### Prerequisites

- Node.js 18+ and npm/yarn/pnpm
- Backend services running (Gateway on port 8080)

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

The app will be available at `http://localhost:3000`

## Project Structure

```
frontend/
├── src/
│   ├── components/      # Reusable components
│   │   ├── common/      # Common UI components
│   │   ├── category/    # Category components
│   │   ├── folder/      # Folder components
│   │   ├── media/       # Media upload components
│   │   ├── tts/         # TTS components
│   │   └── video/       # Video components
│   ├── pages/           # Page components
│   ├── services/        # API services
│   ├── utils/           # Utility functions
│   └── styles/          # Global styles
```

## API Integration

All API calls go through the Gateway Service at `http://localhost:8080/api/catalog`

### Endpoints Used

- `GET /allCategories` - Get all categories
- `POST /categories` - Create category
- `POST /allCategories/{id}/folders` - Create folder
- `POST /allCategories/{id}/folders/{name}/upload` - Upload images
- `POST /allCategories/{id}/folders/{name}/tts` - Generate TTS
- `POST /allCategories/{id}/folders/{name}/generateVideo` - Generate video
- `POST /uploadVideos` - Upload videos
- `GET /videos` - List videos
- `DELETE /videos/{name}` - Delete video
- `POST /convert_videos` - Merge videos
- `POST /finish` - Clear uploads folder

## Workflow

1. **Create Category** → Categories Page
2. **Create Folder** → Category Detail Page
3. **Upload Images** → Folder Detail Page (Images tab)
4. **Generate TTS** → Folder Detail Page (TTS tab)
5. **Generate Video** → Folder Detail Page (Video tab)
6. **Upload More Videos** → Media Library
7. **Merge Videos** → Video Studio

## Development

### Adding New Components

1. Create component in appropriate folder under `src/components/`
2. Export component
3. Import and use in pages

### API Service Updates

Update services in `src/services/api/` to match backend changes.

## License

MIT

