package org.khushal.catalogservice.controller;

import org.khushal.catalogservice.model.Media;
import org.khushal.catalogservice.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    /**
     * Upload images to a specific category and folder
     * Images will be saved with sequential numbering (001.jpg, 002.jpg, etc.)
     */
    @PostMapping("/{categoryName}/{folderName}/images")
    public ResponseEntity<?> uploadImages(@PathVariable String categoryName,
                                         @PathVariable String folderName,
                                         @RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body("No files provided");
            }

            // Validate all files are images
            for (MultipartFile file : files) {
                mediaService.validateFile(file, Media.MediaType.IMAGE);
            }

            List<String> uploadedFiles = mediaService.uploadImages(categoryName, folderName, files);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Images uploaded successfully");
            response.put("uploadedCount", uploadedFiles.size());
            response.put("files", uploadedFiles);
            response.put("category", categoryName);
            response.put("folder", folderName);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload videos to a specific category and folder
     */
    @PostMapping("/{categoryName}/{folderName}/videos")
    public ResponseEntity<?> uploadVideos(@PathVariable String categoryName,
                                         @PathVariable String folderName,
                                         @RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body("No files provided");
            }

            // Validate all files are videos
            for (MultipartFile file : files) {
                mediaService.validateFile(file, Media.MediaType.VIDEO);
            }

            List<String> uploadedFiles = mediaService.uploadVideos(categoryName, folderName, files);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Videos uploaded successfully");
            response.put("uploadedCount", uploadedFiles.size());
            response.put("files", uploadedFiles);
            response.put("category", categoryName);
            response.put("folder", folderName);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    /**
     * List all images in a specific category and folder
     */
    @GetMapping("/{categoryName}/{folderName}/images")
    public ResponseEntity<?> listImages(@PathVariable String categoryName,
                                       @PathVariable String folderName) {
        try {
            List<String> images = mediaService.listImages(categoryName, folderName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", categoryName);
            response.put("folder", folderName);
            response.put("imageCount", images.size());
            response.put("images", images);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to list images: " + e.getMessage()));
        }
    }

    /**
     * List all videos in a specific category and folder
     */
    @GetMapping("/{categoryName}/{folderName}/videos")
    public ResponseEntity<?> listVideos(@PathVariable String categoryName,
                                       @PathVariable String folderName) {
        try {
            List<String> videos = mediaService.listVideos(categoryName, folderName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", categoryName);
            response.put("folder", folderName);
            response.put("videoCount", videos.size());
            response.put("videos", videos);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to list videos: " + e.getMessage()));
        }
    }

    /**
     * Get media statistics for a specific category and folder
     */
    @GetMapping("/{categoryName}/{folderName}/statistics")
    public ResponseEntity<?> getMediaStatistics(@PathVariable String categoryName,
                                               @PathVariable String folderName) {
        try {
            Map<String, Object> statistics = mediaService.getMediaStatistics(categoryName, folderName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", categoryName);
            response.put("folder", folderName);
            response.putAll(statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }

    /**
     * Check if a specific file exists
     */
    @GetMapping("/{categoryName}/{folderName}/exists/{fileName}")
    public ResponseEntity<?> checkFileExists(@PathVariable String categoryName,
                                            @PathVariable String folderName,
                                            @PathVariable String fileName) {
        try {
            boolean exists = mediaService.fileExists(categoryName, folderName, fileName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", categoryName);
            response.put("folder", folderName);
            response.put("fileName", fileName);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check file existence: " + e.getMessage()));
        }
    }

    /**
     * Get the full path of a specific file
     */
    @GetMapping("/{categoryName}/{folderName}/path/{fileName}")
    public ResponseEntity<?> getFilePath(@PathVariable String categoryName,
                                        @PathVariable String folderName,
                                        @PathVariable String fileName) {
        try {
            Path filePath = mediaService.getFilePath(categoryName, folderName, fileName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", categoryName);
            response.put("folder", folderName);
            response.put("fileName", fileName);
            response.put("filePath", filePath.toString());
            response.put("exists", mediaService.fileExists(categoryName, folderName, fileName));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get file path: " + e.getMessage()));
        }
    }

    /**
     * Delete a specific file
     */
    @DeleteMapping("/{categoryName}/{folderName}/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String categoryName,
                                       @PathVariable String folderName,
                                       @PathVariable String fileName) {
        try {
            boolean deleted = mediaService.deleteFile(categoryName, folderName, fileName);
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "File deleted successfully");
                response.put("category", categoryName);
                response.put("folder", folderName);
                response.put("fileName", fileName);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file: " + e.getMessage()));
        }
    }

    /**
     * Upload mixed media (both images and videos) to a specific category and folder
     */
    @PostMapping("/{categoryName}/{folderName}/mixed")
    public ResponseEntity<?> uploadMixedMedia(@PathVariable String categoryName,
                                             @PathVariable String folderName,
                                             @RequestParam(value = "images", required = false) MultipartFile[] imageFiles,
                                             @RequestParam(value = "videos", required = false) MultipartFile[] videoFiles) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("category", categoryName);
            response.put("folder", folderName);
            
            int totalUploaded = 0;
            
            // Upload images if provided
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile file : imageFiles) {
                    mediaService.validateFile(file, Media.MediaType.IMAGE);
                }
                List<String> uploadedImages = mediaService.uploadImages(categoryName, folderName, imageFiles);
                response.put("uploadedImages", uploadedImages);
                response.put("imageCount", uploadedImages.size());
                totalUploaded += uploadedImages.size();
            }
            
            // Upload videos if provided
            if (videoFiles != null && videoFiles.length > 0) {
                for (MultipartFile file : videoFiles) {
                    mediaService.validateFile(file, Media.MediaType.VIDEO);
                }
                List<String> uploadedVideos = mediaService.uploadVideos(categoryName, folderName, videoFiles);
                response.put("uploadedVideos", uploadedVideos);
                response.put("videoCount", uploadedVideos.size());
                totalUploaded += uploadedVideos.size();
            }
            
            if (totalUploaded == 0) {
                return ResponseEntity.badRequest().body("No files provided");
            }
            
            response.put("message", "Mixed media uploaded successfully");
            response.put("totalUploaded", totalUploaded);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}
