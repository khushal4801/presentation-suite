package org.khushal.catalogservice.service;

import org.khushal.catalogservice.model.Media;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MediaService {

    // Base directory for media storage
    private static final Path BASE_DIR = Paths.get("public", "images");
    
    // Supported file extensions
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "avi", "mov", "wmv", "mkv", "flv", "webm");
    
    // Counter for video files
    private final AtomicInteger videoCounter = new AtomicInteger(1);

    /**
     * Upload images and save them with sequential numbering (001.jpg, 002.jpg, etc.)
     * This ensures proper ordering for video generation
     */
    public List<String> uploadImages(String categoryName, String folderName, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }

        Path categoryDir = BASE_DIR.resolve(categoryName);
        Path folderPath = categoryDir.resolve(folderName);
        
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + e.getMessage());
        }

        // Get next sequence number for this folder
        int nextSequence = getNextImageSequence(folderPath);
        
        List<String> uploadedFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                if (originalName == null) {
                    throw new IllegalArgumentException("File name cannot be null");
                }

                String extension = getFileExtension(originalName).toLowerCase();
                if (!IMAGE_EXTENSIONS.contains(extension)) {
                    throw new IllegalArgumentException("Unsupported image format: " + extension + 
                                                     ". Supported: " + IMAGE_EXTENSIONS);
                }

                // Create sequential filename: 001.jpg, 002.jpg, etc.
                String fileName = String.format("%03d.%s", nextSequence++, extension);
                Path destination = folderPath.resolve(fileName);
                
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                uploadedFiles.add(fileName);
                
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename() + 
                                         ". Error: " + e.getMessage());
            }
        }

        return uploadedFiles;
    }

    /**
     * Upload videos and save them with unique names
     */
    public List<String> uploadVideos(String categoryName, String folderName, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }

        Path categoryDir = BASE_DIR.resolve(categoryName);
        Path folderPath = categoryDir.resolve(folderName);
        
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + e.getMessage());
        }

        List<String> uploadedFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                if (originalName == null) {
                    throw new IllegalArgumentException("File name cannot be null");
                }

                String extension = getFileExtension(originalName).toLowerCase();
                if (!VIDEO_EXTENSIONS.contains(extension)) {
                    throw new IllegalArgumentException("Unsupported video format: " + extension + 
                                                     ". Supported: " + VIDEO_EXTENSIONS);
                }

                // Create unique filename with timestamp and counter
                String fileName = String.format("video_%d_%d.%s", 
                    System.currentTimeMillis(), 
                    videoCounter.getAndIncrement(), 
                    extension);
                
                Path destination = folderPath.resolve(fileName);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                uploadedFiles.add(fileName);
                
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename() + 
                                         ". Error: " + e.getMessage());
            }
        }

        return uploadedFiles;
    }

    /**
     * List all image files in a category/folder, sorted by sequence number
     */
    public List<String> listImages(String categoryName, String folderName) {
        Path folderPath = BASE_DIR.resolve(categoryName).resolve(folderName);
        
        if (!Files.exists(folderPath)) {
            return Collections.emptyList();
        }

        List<String> imageFiles = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString();
                    String extension = getFileExtension(fileName).toLowerCase();
                    
                    if (IMAGE_EXTENSIONS.contains(extension)) {
                        imageFiles.add(fileName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to list images: " + e.getMessage());
        }

        // Sort by filename (which includes sequence number)
        imageFiles.sort(String::compareTo);
        return imageFiles;
    }

    /**
     * List all video files in a category/folder
     */
    public List<String> listVideos(String categoryName, String folderName) {
        Path folderPath = BASE_DIR.resolve(categoryName).resolve(folderName);
        
        if (!Files.exists(folderPath)) {
            return Collections.emptyList();
        }

        List<String> videoFiles = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString();
                    String extension = getFileExtension(fileName).toLowerCase();
                    
                    if (VIDEO_EXTENSIONS.contains(extension)) {
                        videoFiles.add(fileName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to list videos: " + e.getMessage());
        }

        videoFiles.sort(String::compareTo);
        return videoFiles;
    }

    /**
     * Get the full path to a specific file
     */
    public Path getFilePath(String categoryName, String folderName, String fileName) {
        return BASE_DIR.resolve(categoryName).resolve(folderName).resolve(fileName);
    }

    /**
     * Check if a file exists
     */
    public boolean fileExists(String categoryName, String folderName, String fileName) {
        Path filePath = getFilePath(categoryName, folderName, fileName);
        return Files.exists(filePath);
    }

    /**
     * Delete a specific file
     */
    public boolean deleteFile(String categoryName, String folderName, String fileName) {
        Path filePath = getFilePath(categoryName, folderName, fileName);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Get media statistics for a category/folder
     */
    public Map<String, Object> getMediaStatistics(String categoryName, String folderName) {
        Map<String, Object> stats = new HashMap<>();
        
        List<String> images = listImages(categoryName, folderName);
        List<String> videos = listVideos(categoryName, folderName);
        
        stats.put("imageCount", images.size());
        stats.put("videoCount", videos.size());
        stats.put("totalMedia", images.size() + videos.size());
        stats.put("images", images);
        stats.put("videos", videos);
        
        return stats;
    }

    /**
     * Get the next sequence number for images in a folder
     */
    private int getNextImageSequence(Path folderPath) {
        if (!Files.exists(folderPath)) {
            return 1;
        }

        int maxSequence = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString();
                    
                    // Check if it's a numbered image file (001.jpg, 002.png, etc.)
                    if (fileName.matches("\\d{3}\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
                        try {
                            int sequence = Integer.parseInt(fileName.substring(0, 3));
                            if (sequence > maxSequence) {
                                maxSequence = sequence;
                            }
                        } catch (NumberFormatException ignored) {
                            // Skip files that don't match the pattern
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan folder for sequence numbers: " + e.getMessage());
        }

        return maxSequence + 1;
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * Determine media type from file extension
     */
    public Media.MediaType getMediaType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return Media.MediaType.IMAGE;
        } else if (VIDEO_EXTENSIONS.contains(extension)) {
            return Media.MediaType.VIDEO;
        }
        
        throw new IllegalArgumentException("Unsupported file type: " + extension);
    }

    /**
     * Validate file before upload
     */
    public void validateFile(MultipartFile file, Media.MediaType expectedType) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        String extension = getFileExtension(originalName).toLowerCase();
        Media.MediaType actualType = getMediaType(originalName);
        
        if (actualType != expectedType) {
            throw new IllegalArgumentException("File type mismatch. Expected: " + expectedType + 
                                             ", but got: " + actualType);
        }
    }
}
