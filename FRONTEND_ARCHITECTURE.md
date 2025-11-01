# React Frontend Architecture Suggestions

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── common/
│   │   │   ├── Header.jsx
│   │   │   ├── Sidebar.jsx
│   │   │   ├── LoadingSpinner.jsx
│   │   │   ├── ErrorMessage.jsx
│   │   │   └── ConfirmDialog.jsx
│   │   ├── category/
│   │   │   ├── CategoryList.jsx
│   │   │   ├── CategoryCard.jsx
│   │   │   ├── CreateCategoryModal.jsx
│   │   │   └── CategoryForm.jsx
│   │   ├── folder/
│   │   │   ├── FolderList.jsx
│   │   │   ├── FolderCard.jsx
│   │   │   ├── CreateFolderModal.jsx
│   │   │   └── FolderForm.jsx
│   │   ├── media/
│   │   │   ├── ImageUpload.jsx
│   │   │   ├── ImageGallery.jsx
│   │   │   ├── ImagePreview.jsx
│   │   │   ├── VideoUpload.jsx
│   │   │   ├── VideoList.jsx
│   │   │   └── VideoPlayer.jsx
│   │   ├── tts/
│   │   │   ├── TTSGenerator.jsx
│   │   │   ├── TTSForm.jsx
│   │   │   └── AudioPlayer.jsx
│   │   ├── video/
│   │   │   ├── VideoGenerator.jsx
│   │   │   ├── VideoMerge.jsx
│   │   │   ├── VideoProgress.jsx
│   │   │   └── VideoOutput.jsx
│   │   └── workflow/
│   │       ├── WorkflowWizard.jsx
│   │       ├── StepIndicator.jsx
│   │       └── WorkflowSummary.jsx
│   ├── pages/
│   │   ├── Dashboard.jsx
│   │   ├── CategoriesPage.jsx
│   │   ├── CategoryDetailPage.jsx
│   │   ├── FolderDetailPage.jsx
│   │   ├── MediaLibraryPage.jsx
│   │   ├── VideoStudioPage.jsx
│   │   └── SettingsPage.jsx
│   ├── services/
│   │   ├── api/
│   │   │   ├── categoryService.js
│   │   │   ├── folderService.js
│   │   │   ├── mediaService.js
│   │   │   ├── ttsService.js
│   │   │   ├── videoService.js
│   │   │   └── uploadService.js
│   │   └── httpClient.js
│   ├── hooks/
│   │   ├── useCategories.js
│   │   ├── useFolders.js
│   │   ├── useMedia.js
│   │   ├── useTTS.js
│   │   ├── useVideo.js
│   │   └── useUpload.js
│   ├── context/
│   │   ├── AppContext.jsx
│   │   └── AuthContext.jsx (if needed)
│   ├── utils/
│   │   ├── constants.js
│   │   ├── helpers.js
│   │   └── validators.js
│   ├── styles/
│   │   ├── components/
│   │   ├── pages/
│   │   └── index.css
│   ├── App.jsx
│   └── index.js
├── package.json
└── README.md
```

---

## Component Breakdown

### 1. **Common Components**

#### Header.jsx
- Navigation bar
- Logo/branding
- User menu (if auth needed)
- Quick actions dropdown

#### Sidebar.jsx
- Main navigation menu
- Links to: Dashboard, Categories, Media Library, Video Studio
- Collapsible menu items

#### LoadingSpinner.jsx
- Reusable loading indicator
- Props: size, color, fullScreen

#### ErrorMessage.jsx
- Display API errors
- Props: message, onRetry, onDismiss

#### ConfirmDialog.jsx
- Reusable confirmation modal
- For delete actions, cleanup actions

---

### 2. **Category Components**

#### CategoryList.jsx
- Displays all categories
- Grid/List view toggle
- Search/filter functionality
- "Create Category" button
- Uses: CategoryCard

#### CategoryCard.jsx
- Individual category display
- Shows: name, folder count, created date
- Actions: View, Edit, Delete
- Clickable to navigate to category detail

#### CreateCategoryModal.jsx
- Modal for creating new category
- Uses: CategoryForm
- Form validation
- API integration

#### CategoryForm.jsx
- Reusable form component
- Fields: category name
- Validation
- Submit handler

---

### 3. **Folder Components**

#### FolderList.jsx
- Lists folders within a category
- Displayed on CategoryDetailPage
- "Create Folder" button
- Search/filter
- Uses: FolderCard

#### FolderCard.jsx
- Individual folder display
- Shows: name, image count, video count, last modified
- Status indicators
- Actions: View, Delete
- Navigate to folder detail

#### CreateFolderModal.jsx
- Modal for creating folder
- Uses: FolderForm
- Validation and API call

#### FolderForm.jsx
- Form fields: folder name
- Validation

---

### 4. **Media Components**

#### ImageUpload.jsx
- Drag & drop file upload
- Multiple file selection
- Preview thumbnails
- Progress indicators
- Upload queue management
- **Endpoint**: `POST /allCategories/{id}/folders/{name}/upload`

#### ImageGallery.jsx
- Grid layout of images
- Lightbox/modal viewer
- Image reordering (for numbering)
- Bulk selection
- Delete multiple images

#### ImagePreview.jsx
- Individual image preview
- Rename functionality (for numbering: 001.jpg, 002.jpg)
- Delete button
- Drag handle for reordering

#### VideoUpload.jsx
- Upload videos to uploads folder
- Single/multiple upload
- Progress tracking
- Video preview
- **Endpoint**: `POST /uploadVideos`

#### VideoList.jsx
- List all videos from uploads folder
- Video cards with thumbnails
- Play button
- Delete button
- **Endpoint**: `GET /videos`

#### VideoPlayer.jsx
- Video playback component
- Controls: play, pause, volume, fullscreen
- Time display
- Used in: VideoList, VideoOutput

---

### 5. **TTS Components**

#### TTSGenerator.jsx
- Main TTS component
- Text input area
- Character counter
- Language selection (if multiple)
- Generate button
- **Endpoint**: `POST /allCategories/{id}/folders/{name}/tts`
- Uses: TTSForm, AudioPlayer

#### TTSForm.jsx
- Text input field
- Text formatting options
- Preview text
- Validation

#### AudioPlayer.jsx
- Play generated audio
- Waveform visualization (optional)
- Download button
- Playback controls

---

### 6. **Video Components**

#### VideoGenerator.jsx
- Main video generation component
- Shows: images count, audio status
- Generate button
- Progress indicator
- **Endpoint**: `POST /allCategories/{id}/folders/{name}/generateVideo`
- Status checks before generation

#### VideoMerge.jsx
- Merge all videos with background music
- Shows: video list preview
- Background music selector
- Merge button
- Progress tracking
- **Endpoint**: `POST /convert_videos`
- Uses: VideoList

#### VideoProgress.jsx
- Progress bar for video operations
- Shows: current step, percentage, ETA
- Cancel button (if supported)

#### VideoOutput.jsx
- Display generated video
- Video player
- Download button
- Share options
- Output path display

---

### 7. **Workflow Components**

#### WorkflowWizard.jsx
- Step-by-step wizard for complete flow
- Steps:
  1. Select/Create Category
  2. Create Folder
  3. Upload Images
  4. Generate TTS
  5. Generate Video
  6. Merge Videos (optional)
- Progress tracking
- Navigation between steps

#### StepIndicator.jsx
- Visual step indicator
- Shows current step
- Completed steps
- Disabled steps

#### WorkflowSummary.jsx
- Summary of workflow completion
- Shows: category, folder, images count, video output
- Actions: Download, Share, Start New

---

## Pages Structure

### Dashboard.jsx
- Overview of all categories
- Recent folders
- Quick stats
- Quick actions
- Recent videos

### CategoriesPage.jsx
- Main categories view
- Uses: CategoryList, CreateCategoryModal
- Filters and search

### CategoryDetailPage.jsx
- Shows category details
- Folder list for category
- Uses: FolderList, CreateFolderModal
- Breadcrumb navigation

### FolderDetailPage.jsx
- Main workspace page
- Tabs:
  - Images (ImageGallery, ImageUpload)
  - TTS (TTSGenerator)
  - Video (VideoGenerator)
- Sidebar with folder info
- Uses: ImageUpload, ImageGallery, TTSGenerator, VideoGenerator

### MediaLibraryPage.jsx
- All uploaded videos
- Uses: VideoList, VideoUpload
- Filters and search
- Bulk actions

### VideoStudioPage.jsx
- Video merging interface
- Uses: VideoMerge, VideoList
- Background music upload
- Output preview

---

## Services/API Layer

### httpClient.js
```javascript
// Axios instance with base URL
// Request/Response interceptors
// Error handling
// Token management (if auth needed)
```

### categoryService.js
- `getAllCategories()` → `GET /allCategories`
- `getCategoryById(id)` → `GET /categories/{id}`
- `createCategory(data)` → `POST /categories`
- `updateCategory(id, data)` → `PUT /categories/{id}`
- `deleteCategory(id)` → `DELETE /categories/{id}`

### folderService.js
- `createFolder(categoryId, folderName)` → `POST /allCategories/{id}/folders`
- `getFolders(categoryId)` → (might need custom endpoint)

### mediaService.js
- `uploadImages(categoryId, folderName, files)` → `POST /allCategories/{id}/folders/{name}/upload`
- `uploadVideos(files)` → `POST /uploadVideos`
- `listVideos()` → `GET /videos`
- `deleteVideo(videoName)` → `DELETE /videos/{name}`

### ttsService.js
- `generateTTS(categoryId, folderName, text)` → `POST /allCategories/{id}/folders/{name}/tts`

### videoService.js
- `generateVideo(categoryId, folderName)` → `POST /allCategories/{id}/folders/{name}/generateVideo`
- `mergeVideos()` → `POST /convert_videos`
- `finish()` → `POST /finish`

---

## Custom Hooks

### useCategories.js
```javascript
- useCategories() - Fetch and manage categories
- useCategory(id) - Single category with refetch
- useCreateCategory() - Mutation hook
- useDeleteCategory() - Delete mutation
```

### useFolders.js
```javascript
- useFolders(categoryId) - Fetch folders for category
- useCreateFolder() - Create folder mutation
```

### useMedia.js
```javascript
- useUploadImages() - Image upload with progress
- useUploadVideos() - Video upload with progress
- useVideos() - Fetch video list
- useDeleteVideo() - Delete video mutation
```

### useTTS.js
```javascript
- useGenerateTTS() - TTS generation mutation
- useTTSStatus() - Check TTS generation status
```

### useVideo.js
```javascript
- useGenerateVideo() - Video generation mutation
- useMergeVideos() - Video merge mutation
- useVideoProgress() - Track video generation progress
```

### useUpload.js
```javascript
- useFileUpload() - Generic file upload hook
- Progress tracking
- Error handling
- Queue management
```

---

## State Management Options

### Option 1: React Query (Recommended)
- Server state management
- Caching and refetching
- Optimistic updates
- Easy error handling
- Loading states

### Option 2: Redux Toolkit
- Global state management
- If complex client-side state needed
- With RTK Query for API calls

### Option 3: Context API + Custom Hooks
- Simpler approach
- Good for medium complexity
- Combine with React Query

---

## UI Library Suggestions

### Option 1: Material-UI (MUI)
- Comprehensive component library
- Good documentation
- Customizable theming
- Components: Cards, Dialogs, Progress, etc.

### Option 2: Ant Design
- Enterprise-grade components
- Rich component set
- Good form handling
- Upload components built-in

### Option 3: Chakra UI
- Simple and modular
- Good for rapid development
- Accessible by default

### Option 4: Tailwind CSS + Headless UI
- Full control over styling
- Utility-first approach
- Flexible and modern

---

## Routing Structure

```javascript
/ → Dashboard
/categories → CategoriesPage
/categories/:categoryId → CategoryDetailPage
/categories/:categoryId/folders/:folderName → FolderDetailPage
/media → MediaLibraryPage
/studio → VideoStudioPage
/settings → SettingsPage
```

---

## Key Features to Implement

### 1. **File Upload**
- Drag & drop
- Multiple file selection
- Progress bars
- Preview before upload
- File validation (size, type)
- Queue management

### 2. **Image Management**
- Numbered image renaming (001.jpg, 002.jpg)
- Drag to reorder
- Bulk operations
- Preview gallery
- Delete with confirmation

### 3. **TTS Integration**
- Text editor with formatting
- Character/word count
- Preview before generation
- Audio playback
- Download generated audio

### 4. **Video Generation**
- Status checking (images, audio ready?)
- Progress tracking
- Real-time updates (WebSocket/SSE if needed)
- Video preview
- Download generated video

### 5. **Video Merging**
- Video list display
- Background music upload
- Merge progress
- Output preview
- Download final video

### 6. **Error Handling**
- API error display
- Retry mechanisms
- Validation errors
- Network error handling
- User-friendly error messages

### 7. **Loading States**
- Skeleton loaders
- Progress indicators
- Button loading states
- Full-page loaders

---

## API Integration Pattern

```javascript
// Example with React Query
const { data, isLoading, error, refetch } = useQuery(
  'categories',
  () => categoryService.getAllCategories(),
  {
    refetchOnWindowFocus: false,
    staleTime: 5 * 60 * 1000, // 5 minutes
  }
);

const createCategoryMutation = useMutation(
  (data) => categoryService.createCategory(data),
  {
    onSuccess: () => {
      queryClient.invalidateQueries('categories');
      toast.success('Category created successfully');
    },
    onError: (error) => {
      toast.error(error.message);
    }
  }
);
```

---

## Recommended Tech Stack

### Core
- **React 18+** - Latest React features
- **React Router v6** - Navigation
- **React Query (TanStack Query)** - Server state
- **Axios** - HTTP client

### UI Library
- **Material-UI (MUI)** or **Ant Design**
- **React Dropzone** - File uploads
- **React Player** - Video playback
- **React Audio Player** - Audio playback

### Utilities
- **React Hook Form** - Form management
- **Zod** or **Yup** - Validation
- **Date-fns** - Date formatting
- **React Hot Toast** - Notifications

### Development
- **Vite** - Build tool (faster than CRA)
- **ESLint** - Linting
- **Prettier** - Code formatting

---

## Component Priority (MVP)

### Phase 1: Core Functionality
1. CategoryList
2. CreateCategoryModal
3. FolderList
4. CreateFolderModal
5. ImageUpload
6. ImageGallery

### Phase 2: TTS & Video
7. TTSGenerator
8. VideoGenerator
9. VideoList

### Phase 3: Advanced Features
10. VideoMerge
11. WorkflowWizard
12. Dashboard

---

## Additional Considerations

### 1. **Real-time Updates**
- Consider WebSocket/SSE for video generation progress
- Polling for long-running operations

### 2. **File Management**
- Image numbering UI (rename to 001.jpg, 002.jpg)
- Bulk operations
- Drag and drop reordering

### 3. **Responsive Design**
- Mobile-friendly layouts
- Touch gestures for mobile
- Adaptive grid layouts

### 4. **Performance**
- Image lazy loading
- Video lazy loading
- Pagination for large lists
- Virtual scrolling for long lists

### 5. **Accessibility**
- ARIA labels
- Keyboard navigation
- Screen reader support
- Focus management

---

## Example Component Structure

```jsx
// CategoryList.jsx (Example)
import { useQuery } from '@tanstack/react-query';
import { categoryService } from '../services/api/categoryService';
import CategoryCard from './CategoryCard';
import CreateCategoryModal from './CreateCategoryModal';
import LoadingSpinner from '../common/LoadingSpinner';
import ErrorMessage from '../common/ErrorMessage';

const CategoryList = () => {
  const { data: categories, isLoading, error } = useQuery(
    ['categories'],
    categoryService.getAllCategories
  );

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      <CreateCategoryModal />
      <div className="grid">
        {categories?.map(category => (
          <CategoryCard key={category.id} category={category} />
        ))}
      </div>
    </div>
  );
};
```

---

This architecture provides a scalable, maintainable structure for your React frontend that covers all the backend endpoints and provides a smooth user experience for the video presentation workflow.

