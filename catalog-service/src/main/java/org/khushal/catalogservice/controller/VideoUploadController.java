package org.khushal.catalogservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class VideoUploadController {

    private final Path uploadDir = Paths.get("uploads");
    private final Path videosDir = Paths.get("videos");
    private final Path audioDir = Paths.get("audio");
    private final AtomicInteger videoCounter = new AtomicInteger(1);

    @PostMapping("/uploadVideos")
    public ResponseEntity<String> uploadVideos(@RequestParam("video")MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No files uploaded.");
        }

        try {
            Files.createDirectories(uploadDir); // Ensure the upload directory exists

            for(MultipartFile file : files) {
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body("One or more files are empty.");
                }

                // Generate a unique filename
                String originalName = file.getOriginalFilename();
                if (originalName == null || !originalName.toLowerCase().endsWith(".mp4")) {
                    return ResponseEntity.badRequest().body("Only .mp4 video files are allowed");
                }
                int count = videoCounter.getAndIncrement();
                String filename = "video" + count + ".mp4";
                Path targetPath = uploadDir.resolve(filename);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            }
            return ResponseEntity.ok("Video upload endpoint is under construction.");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files: " + e.getMessage());
        }
    }

    /**
     * Merge/concatenate all videos from uploads folder with background music
     * Matches Node.js endpoint: POST /convert_videos
     */
    @PostMapping("/convert_videos")
    public ResponseEntity<?> convertVideos() {
        try {
            if (!Files.exists(uploadDir)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Uploads directory not found");
            }

            // Get all video files and sort by number
            List<Path> videoFiles = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        String fileName = file.getFileName().toString();
                        if (fileName.matches("video\\d+\\.mp4")) {
                            videoFiles.add(file);
                        }
                    }
                }
            }

            if (videoFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("No video files found in uploads folder");
            }

            // Sort by video number
            videoFiles.sort((a, b) -> {
                Pattern pattern = Pattern.compile("video(\\d+)\\.mp4");
                Matcher matcherA = pattern.matcher(a.getFileName().toString());
                Matcher matcherB = pattern.matcher(b.getFileName().toString());
                if (matcherA.find() && matcherB.find()) {
                    return Integer.compare(Integer.parseInt(matcherA.group(1)), Integer.parseInt(matcherB.group(1)));
                }
                return 0;
            });

            // Create file list for ffmpeg concat
            Path fileListPath = uploadDir.resolve("filelist.txt");
            StringBuilder fileListContent = new StringBuilder();
            for (Path videoFile : videoFiles) {
                fileListContent.append("file '").append(videoFile.toAbsolutePath().toString().replace("\\", "/")).append("'\n");
            }
            Files.writeString(fileListPath, fileListContent.toString());

            // Check if background music exists
            Path backgroundMusic = audioDir.resolve("background-music.mp3");
            if (!Files.exists(backgroundMusic)) {
                Files.deleteIfExists(fileListPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Background music file not found: " + backgroundMusic);
            }

            // Create output file
            Files.createDirectories(videosDir);
            String outputFileName = System.currentTimeMillis() + "-output.mp4";
            Path outputFilePath = videosDir.resolve(outputFileName);

            // Execute ffmpeg command to merge videos with background music
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-safe", "0",
                    "-f", "concat",
                    "-i", fileListPath.toString(),
                    "-i", backgroundMusic.toString(),
                    "-filter_complex", "[0:a]aformat=fltp:44100:stereo,volume=0.5[a1];[1:a]aformat=fltp:44100:stereo,volume=0.5[a2];[a1][a2]amerge=inputs=2[aout]",
                    "-map", "0:v",
                    "-map", "[aout]",
                    "-c:v", "libx264",
                    "-preset", "ultrafast",
                    "-crf", "22",
                    "-y",
                    outputFilePath.toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read process output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFmpeg] " + line);
                }
            }

            int exitCode = process.waitFor();

            // Clean up file list
            Files.deleteIfExists(fileListPath);

            if (exitCode == 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Videos in " + uploadDir + " have been concatenated successfully.");
                response.put("outputFilePath", outputFilePath.toString());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while concatenating the videos.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error converting videos: " + e.getMessage());
        }
    }

    /**
     * List all videos in uploads folder
     * Matches Node.js endpoint: GET /videos
     */
    @GetMapping("/videos")
    public ResponseEntity<?> listVideos() {
        try {
            if (!Files.exists(uploadDir)) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, String>> videos = new ArrayList<>();
            List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mkv");

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        String fileName = file.getFileName().toString();
                        String extension = fileName.substring(fileName.lastIndexOf("."));
                        if (videoExtensions.contains(extension.toLowerCase())) {
                            Map<String, String> video = new HashMap<>();
                            video.put("name", fileName.substring(0, fileName.lastIndexOf(".")));
                            video.put("path", file.toString().replace("\\", "/"));
                            videos.add(video);
                        }
                    }
                }
            }

            return ResponseEntity.ok(videos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error listing videos: " + e.getMessage());
        }
    }

    /**
     * Delete a specific video
     * Matches Node.js endpoint: DELETE /videos/:videoName
     */
    @DeleteMapping("/videos/{videoName}")
    public ResponseEntity<?> deleteVideo(@PathVariable String videoName) {
        try {
            Path videoPath = uploadDir.resolve(videoName + ".mp4");

            if (!Files.exists(videoPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
            }

            Files.delete(videoPath);
            return ResponseEntity.ok("Video deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting video: " + e.getMessage());
        }
    }

    /**
     * Cleanup/reset uploads folder
     * Matches Node.js endpoint: POST /finish
     */
    @PostMapping("/finish")
    public ResponseEntity<?> finish() {
        try {
            if (!Files.exists(uploadDir)) {
                videoCounter.set(1);
                return ResponseEntity.ok("Upload folder has been emptied");
            }

            // Delete all files in uploads folder
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        Files.delete(file);
                    }
                }
            }

            // Reset video counter
            videoCounter.set(1);

            return ResponseEntity.ok("Upload folder has been emptied");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error emptying upload folder: " + e.getMessage());
        }
    }
}
