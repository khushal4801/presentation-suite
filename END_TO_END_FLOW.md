# End-to-End Flow Documentation

## Microservices Architecture

This document describes the complete end-to-end flow connecting all microservices in the Presentation Suite application.

### Microservices Overview

1. **Gateway Service** (Port 8080) - API Gateway
2. **Catalog Service** (Port 8081) - Main service for categories, folders, file uploads, TTS, and video generation
3. **Video Service** (Port 8082) - Video processing and generation from images
4. **TTS Service** (Port 8083) - Text-to-Speech service wrapper
5. **TTS FastAPI Service** (Port 8001) - Python FastAPI service for TTS generation
6. **Eureka Server** (Port 8761) - Service discovery

---

## Complete Workflow: Creating a Video Presentation

### Step 1: Create a Category
**Endpoint:** `POST /categories`  
**Service:** Catalog Service  
**Description:** Create a new category

```bash
POST http://localhost:8080/api/catalog/categories
Content-Type: application/json

{
  "name": "Education"
}
```

**Response:**
```json
{
  "id": "category-id-123",
  "name": "Education"
}
```

---

### Step 2: Get All Categories
**Endpoint:** `GET /allCategories`  
**Service:** Catalog Service  
**Description:** List all categories (matches Node.js pattern)

```bash
GET http://localhost:8080/api/catalog/allCategories
```

**Response:**
```json
[
  {
    "id": "category-id-123",
    "name": "Education"
  }
]
```

---

### Step 3: Create a Folder Inside Category
**Endpoint:** `POST /allCategories/{categoryId}/folders`  
**Service:** Catalog Service  
**Description:** Create a folder inside a category

```bash
POST http://localhost:8080/api/catalog/allCategories/category-id-123/folders
Content-Type: application/json

{
  "name": "lesson1"
}
```

**Response:**
```
Folder created: public/images/Education/lesson1
```

---

### Step 4: Upload Images to Folder
**Endpoint:** `POST /allCategories/{categoryId}/folders/{folderName}/upload`  
**Service:** Catalog Service  
**Description:** Upload images to the folder. Images should be numbered as 001.jpg, 002.jpg, etc. for proper ordering.

```bash
POST http://localhost:8080/api/catalog/allCategories/category-id-123/folders/lesson1/upload
Content-Type: multipart/form-data

files: [001.jpg, 002.jpg, 003.jpg]
```

**Response:**
```
3 file(s) uploaded successfully: [filename1.jpg, filename2.jpg, filename3.jpg]
```

**Note:** For proper video generation, rename images to numbered format:
- 001.jpg
- 002.jpg
- 003.jpg
- etc.

---

### Step 5: Generate Text-to-Speech Audio
**Endpoint:** `POST /allCategories/{categoryId}/folders/{folderName}/tts`  
**Service:** Catalog Service → TTS Service → FastAPI TTS  
**Description:** Generate MP3 audio from text

**Flow:**
1. Catalog Service receives request
2. Catalog Service calls TTS Service (or FastAPI directly)
3. TTS Service calls FastAPI TTS service
4. Audio file saved to `public/images/{categoryName}/{folderName}/audio.mp3`

```bash
POST http://localhost:8080/api/catalog/allCategories/category-id-123/folders/lesson1/tts
Content-Type: application/json

{
  "text": "Welcome to this lesson. Today we will learn about Spring Boot microservices."
}
```

**Response:**
```json
{
  "message": "Saved MP3 file: public/images/Education/lesson1/audio.mp3",
  "fileName": "audio.mp3",
  "filePath": "public/images/Education/lesson1/audio.mp3"
}
```

---

### Step 6: Generate Video from Images and Audio
**Endpoint:** `POST /allCategories/{categoryId}/folders/{folderName}/generateVideo`  
**Service:** Catalog Service → Video Service  
**Description:** Generate video from images and audio using FFmpeg

**Flow:**
1. Catalog Service validates category and folder
2. Catalog Service calls Video Service microservice
3. Video Service:
   - Reads numbered images (001.jpg, 002.jpg, etc.)
   - Combines with audio.mp3
   - Uses FFmpeg to generate video
   - Saves to `uploads/video_{category}_{folder}_{counter}.mp4`
   - Cleans up images and audio (as per Node.js behavior)

```bash
POST http://localhost:8080/api/catalog/allCategories/category-id-123/folders/lesson1/generateVideo
```

**Response:**
```json
{
  "message": "Video generated successfully: uploads/video_Education_lesson1_1.mp4",
  "outputPath": "uploads/video_Education_lesson1_1.mp4"
}
```

---

### Step 7: Upload Additional Videos (Optional)
**Endpoint:** `POST /uploadVideos`  
**Service:** Catalog Service  
**Description:** Upload standalone videos to uploads folder

```bash
POST http://localhost:8080/api/catalog/uploadVideos
Content-Type: multipart/form-data

video: [video1.mp4, video2.mp4]
```

**Response:**
```
Video upload endpoint is under construction.
```

Videos are saved as `video1.mp4`, `video2.mp4`, etc. in the `uploads` folder.

---

### Step 8: List All Videos
**Endpoint:** `GET /videos`  
**Service:** Catalog Service  
**Description:** List all videos in the uploads folder

```bash
GET http://localhost:8080/api/catalog/videos
```

**Response:**
```json
[
  {
    "name": "video1",
    "path": "uploads/video1.mp4"
  },
  {
    "name": "video2",
    "path": "uploads/video2.mp4"
  }
]
```

---

### Step 9: Merge All Videos with Background Music
**Endpoint:** `POST /convert_videos`  
**Service:** Catalog Service  
**Description:** Concatenate all videos from uploads folder and merge with background music

**Prerequisites:**
- Background music file must exist at: `audio/background-music.mp3`
- Videos in uploads folder should be named: `video1.mp4`, `video2.mp4`, etc.

**Flow:**
1. Catalog Service reads all video files from `uploads` folder
2. Sorts videos by number (video1, video2, video3...)
3. Creates FFmpeg concat file list
4. Merges videos with background music
5. Outputs final video to `videos/{timestamp}-output.mp4`

```bash
POST http://localhost:8080/api/catalog/convert_videos
```

**Response:**
```json
{
  "message": "Videos in uploads have been concatenated successfully.",
  "outputFilePath": "videos/1234567890-output.mp4"
}
```

---

### Step 10: Delete a Video (Optional)
**Endpoint:** `DELETE /videos/{videoName}`  
**Service:** Catalog Service  
**Description:** Delete a specific video from uploads folder

```bash
DELETE http://localhost:8080/api/catalog/videos/video1
```

**Response:**
```
Video deleted successfully
```

---

### Step 11: Cleanup/Finish
**Endpoint:** `POST /finish`  
**Service:** Catalog Service  
**Description:** Clean up uploads folder and reset video counter

```bash
POST http://localhost:8080/api/catalog/finish
```

**Response:**
```
Upload folder has been emptied
```

**Actions:**
- Deletes all files in `uploads` folder
- Resets video counter to 1

---

## Service Communication Flow

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│ Gateway Service │ (Port 8080)
│  (Eureka Client)│
└──────┬──────────┘
       │
       ├─────────────────────────────────────────────┐
       │                                             │
       ▼                                             ▼
┌─────────────────┐                    ┌─────────────────┐
│ Catalog Service │                    │  Video Service  │
│  (Port 8081)    │                    │  (Port 8082)    │
│                 │                    │                 │
│ - Categories    │                    │ - Video Merge   │
│ - Folders       │                    │ - FFmpeg        │
│ - File Upload   │◄───────────────────┤   Processing   │
│ - TTS           │   HTTP/REST        │                 │
│ - Video Gen     │                    └─────────────────┘
└──────┬──────────┘
       │
       ├──────────────────┐
       │                  │
       ▼                  ▼
┌─────────────────┐  ┌──────────────────┐
│  TTS Service    │  │  FastAPI TTS     │
│  (Port 8083)    │  │  (Port 8001)     │
│                 │  │                  │
│ - TTS Wrapper   │─►│ - gTTS           │
│                 │  │ - MP3 Generation │
└─────────────────┘  └──────────────────┘
```

---

## Configuration

### Catalog Service (`application.yml`)
```yaml
server:
  port: 8081

spring:
  application:
    name: catalog-service

tts:
  fastapi:
    url: http://127.0.0.1:8001

video:
  service:
    url: http://localhost:8082
```

### Gateway Service Routes
All requests go through the gateway:
- `/api/catalog/**` → Catalog Service
- `/api/video/**` → Video Service
- `/api/tts/**` → TTS Service

---

## API Endpoints Summary

### Catalog Service Endpoints (via Gateway)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/catalog/allCategories` | Get all categories |
| POST | `/api/catalog/categories` | Create category |
| POST | `/api/catalog/allCategories/{id}/folders` | Create folder |
| POST | `/api/catalog/allCategories/{id}/folders/{name}/upload` | Upload images |
| POST | `/api/catalog/allCategories/{id}/folders/{name}/tts` | Generate TTS |
| POST | `/api/catalog/allCategories/{id}/folders/{name}/generateVideo` | Generate video |
| POST | `/api/catalog/uploadVideos` | Upload videos |
| GET | `/api/catalog/videos` | List videos |
| DELETE | `/api/catalog/videos/{name}` | Delete video |
| POST | `/api/catalog/convert_videos` | Merge videos |
| POST | `/api/catalog/finish` | Cleanup |

---

## Prerequisites

1. **FFmpeg** - Must be installed and in PATH for video processing
2. **MongoDB** - Running on localhost:27017
3. **Eureka Server** - Running on localhost:8761
4. **Python FastAPI TTS Service** - Running on localhost:8001
5. **Background Music** - File must exist at `audio/background-music.mp3`

---

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK` - Success
- `400 Bad Request` - Invalid input
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Notes

1. **Image Naming**: For video generation, images must be numbered as `001.jpg`, `002.jpg`, etc.
2. **File Paths**: All file paths use forward slashes (`/`) for cross-platform compatibility
3. **Video Counter**: Maintains state across requests, reset on `/finish`
4. **Cleanup**: Video generation automatically cleans up images and audio after generation (matching Node.js behavior)
5. **Microservice Communication**: Uses WebClient for HTTP calls between services

---

## Testing the Complete Flow

```bash
# 1. Create category
curl -X POST http://localhost:8080/api/catalog/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"TestCategory"}'

# 2. Get categories
curl http://localhost:8080/api/catalog/allCategories

# 3. Create folder (use category ID from step 1)
curl -X POST http://localhost:8080/api/catalog/allCategories/{categoryId}/folders \
  -H "Content-Type: application/json" \
  -d '{"name":"testFolder"}'

# 4. Upload images (use multipart/form-data)
curl -X POST http://localhost:8080/api/catalog/allCategories/{categoryId}/folders/testFolder/upload \
  -F "files=@001.jpg" \
  -F "files=@002.jpg"

# 5. Generate TTS
curl -X POST http://localhost:8080/api/catalog/allCategories/{categoryId}/folders/testFolder/tts \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello world"}'

# 6. Generate video
curl -X POST http://localhost:8080/api/catalog/allCategories/{categoryId}/folders/testFolder/generateVideo

# 7. List videos
curl http://localhost:8080/api/catalog/videos

# 8. Merge videos
curl -X POST http://localhost:8080/api/catalog/convert_videos

# 9. Cleanup
curl -X POST http://localhost:8080/api/catalog/finish
```

---

## Microservice Integration Details

### Catalog Service → TTS Service
- **Method**: HTTP POST via WebClient
- **Endpoint**: `http://127.0.0.1:8001/generate-tts` (FastAPI directly)
- **Payload**: `{"text": "..."}`
- **Response**: MP3 file bytes

### Catalog Service → Video Service
- **Method**: HTTP POST via WebClient
- **Endpoint**: `http://localhost:8082/api/video/merge`
- **Parameters**: Query parameters (category, folder, audioPath, etc.)
- **Response**: JSON with video generation details

### Service Discovery
- All services register with Eureka Server
- Gateway uses service discovery for load balancing
- Services can be scaled horizontally

---

This completes the end-to-end flow documentation for the Presentation Suite microservices architecture.

