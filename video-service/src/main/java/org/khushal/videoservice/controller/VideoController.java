package org.khushal.videoservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/video")
public class VideoController {
    
    private final AtomicInteger videoCounter = new AtomicInteger(1);
    
    // Pattern to match numbered image files (001.jpg, 002.png, etc.)
    private static final Pattern NUMBERED_IMAGE_PATTERN = Pattern.compile("^(\\d{3})\\.(jpg|jpeg|png|gif|webp|bmp)$", Pattern.CASE_INSENSITIVE);

    /**
     * Generate video from ordered images and existing audio file
     * Images should be numbered as 001.jpg, 002.jpg, etc. for proper ordering
     */
    @PostMapping("/merge")
    public ResponseEntity<?> mergeImagesWithAudio(
            @RequestParam String category,
            @RequestParam String folder,
            @RequestParam String audioPath,
            @RequestParam(defaultValue = "5") int secondsPerImage,
            @RequestParam(defaultValue = "720") int height,
            @RequestParam(defaultValue = "uploads") String outputDir,
            @RequestParam(defaultValue = "false") boolean cleanupImages,
            @RequestParam(defaultValue = "false") boolean cleanupAudio) {
        
        try {
            // Validate parameters
            if (secondsPerImage <= 0) {
                return ResponseEntity.badRequest().body("Seconds per image must be positive");
            }
            if (height <= 0) {
                return ResponseEntity.badRequest().body("Height must be positive");
            }

            // Check if images directory exists
            Path imagesDir = Paths.get("public", "images", category, folder);
            if (!Files.isDirectory(imagesDir)) {
                return ResponseEntity.status(404).body("Images directory not found: " + imagesDir);
            }

            // Check if audio file exists
            Path audioFile = Paths.get(audioPath);
            if (!Files.isRegularFile(audioFile)) {
                return ResponseEntity.status(404).body("Audio file not found: " + audioFile);
            }

            // Get ordered image files
            List<Path> imageFiles = getOrderedImageFiles(imagesDir);
            if (imageFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("No numbered image files found in: " + imagesDir + 
                    ". Images should be named as 001.jpg, 002.jpg, etc.");
            }

            // Create output directory
            Path outputDirPath = Paths.get(outputDir);
            Files.createDirectories(outputDirPath);

            // Generate unique output filename
            String outputFileName = String.format("video_%s_%s_%d.mp4", 
                category, folder, videoCounter.getAndIncrement());
            Path outputVideoPath = outputDirPath.resolve(outputFileName);

            // Create temporary file list for ffmpeg
            Path fileListPath = imagesDir.resolve("temp_filelist.txt");
            
            try {
                // Create file list with duration for each image
                createImageFileList(fileListPath, imageFiles, secondsPerImage);

                // Execute ffmpeg command
                boolean success = executeFFmpegCommand(fileListPath, audioFile, outputVideoPath, height);

                if (success) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Video generated successfully");
                    response.put("outputPath", outputVideoPath.toString());
                    response.put("category", category);
                    response.put("folder", folder);
                    response.put("imageCount", imageFiles.size());
                    response.put("secondsPerImage", secondsPerImage);
                    response.put("height", height);
                    response.put("audioFile", audioPath);

                    // Cleanup if requested
                    if (cleanupImages) {
                        cleanupImageFiles(imageFiles);
                        response.put("cleanupImages", true);
                    }
                    if (cleanupAudio) {
                        Files.deleteIfExists(audioFile);
                        response.put("cleanupAudio", true);
                    }

                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(500).body("FFmpeg failed to generate video");
                }

            } finally {
                // Always clean up temporary file list
                Files.deleteIfExists(fileListPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error generating video: " + e.getMessage());
        }
    }

    /**
     * Generate video with default settings (legacy endpoint for backward compatibility)
     */
    @PostMapping("/generate/{category}/{folder}")
    public ResponseEntity<?> generateVideoLegacy(@PathVariable String category, 
                                               @PathVariable String folder) {
        return mergeImagesWithAudio(category, folder, "uploads/audio/output.mp3", 5, 720, "uploads", false, false);
    }

    /**
     * Get video generation status and available images
     */
    @GetMapping("/status/{category}/{folder}")
    public ResponseEntity<?> getVideoStatus(@PathVariable String category, 
                                          @PathVariable String folder) {
        try {
            Path imagesDir = Paths.get("public", "images", category, folder);
            
            if (!Files.isDirectory(imagesDir)) {
                return ResponseEntity.status(404).body("Images directory not found: " + imagesDir);
            }

            List<Path> imageFiles = getOrderedImageFiles(imagesDir);
            List<String> imageNames = new ArrayList<>();
            for (Path image : imageFiles) {
                imageNames.add(image.getFileName().toString());
            }

            Map<String, Object> status = new HashMap<>();
            status.put("category", category);
            status.put("folder", folder);
            status.put("imageCount", imageFiles.size());
            status.put("images", imageNames);
            status.put("readyForGeneration", !imageFiles.isEmpty());

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting status: " + e.getMessage());
        }
    }

    /**
     * Get ordered image files from directory
     */
    private List<Path> getOrderedImageFiles(Path imagesDir) throws IOException {
        List<Path> imageFiles = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString();
                    if (NUMBERED_IMAGE_PATTERN.matcher(fileName).matches()) {
                        imageFiles.add(file);
                    }
                }
            }
        }

        // Sort by the numeric prefix (001, 002, 010, etc.)
        imageFiles.sort((a, b) -> {
            String nameA = a.getFileName().toString();
            String nameB = b.getFileName().toString();
            int numA = Integer.parseInt(nameA.substring(0, 3));
            int numB = Integer.parseInt(nameB.substring(0, 3));
            return Integer.compare(numA, numB);
        });

        return imageFiles;
    }

    /**
     * Create file list for ffmpeg with duration for each image
     */
    private void createImageFileList(Path fileListPath, List<Path> imageFiles, int secondsPerImage) throws IOException {
        StringBuilder content = new StringBuilder();
        
        for (Path imageFile : imageFiles) {
            content.append("file '").append(imageFile.toAbsolutePath().toString().replace("\\", "/")).append("'\n");
            content.append("duration ").append(secondsPerImage).append("\n");
        }
        
        // Add the last image again without duration to make concat demuxer work properly
        if (!imageFiles.isEmpty()) {
            Path lastImage = imageFiles.get(imageFiles.size() - 1);
            content.append("file '").append(lastImage.toAbsolutePath().toString().replace("\\", "/")).append("'\n");
        }
        
        Files.writeString(fileListPath, content.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Execute ffmpeg command to merge images with audio
     */
    private boolean executeFFmpegCommand(Path fileListPath, Path audioFile, Path outputVideoPath, int height) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-hide_banner",
                "-y", // Overwrite output file
                "-f", "concat",
                "-safe", "0",
                "-i", fileListPath.toString(),
                "-i", audioFile.toString(),
                "-vf", "scale=-2:" + height + ",format=yuv420p",
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "20",
                "-c:a", "aac",
                "-b:a", "192k",
                "-shortest", // End when shortest input ends
                "-movflags", "+faststart", // Optimize for web streaming
                outputVideoPath.toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Log ffmpeg output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFmpeg] " + line);
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (Exception e) {
            System.err.println("FFmpeg execution failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clean up image files after video generation
     */
    private void cleanupImageFiles(List<Path> imageFiles) {
        for (Path imageFile : imageFiles) {
            try {
                Files.deleteIfExists(imageFile);
                System.out.println("Deleted image: " + imageFile.getFileName());
            } catch (IOException e) {
                System.err.println("Failed to delete image " + imageFile.getFileName() + ": " + e.getMessage());
            }
        }
    }
}
