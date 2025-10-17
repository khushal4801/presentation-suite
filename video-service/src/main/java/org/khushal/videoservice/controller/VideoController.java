package org.khushal.videoservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/video")
public class VideoController {
    private int videoCounter = 1;

    @PostMapping("/allCategories/{category}/folders/{folder}/generate")
    public ResponseEntity<String> generateVideo(@PathVariable String category, @PathVariable String folder) {
        try {
            Path imagesPath = Paths.get("public","images",category,folder);
            Path audioPath  = Paths.get("uploads","audio","output.mp3");
            Path outputVideoPath = Paths.get("uploads", "video" + videoCounter + ".mp4");
            Path fileListPath = imagesPath.resolve("filelist.txt"); // Path to the file list

            // Here you would call the video generation service via ffmpeg library
            // Check if audio file exists
            if (!Files.exists(audioPath)) {
                return ResponseEntity.status(404).body("Audio file not found: " + audioPath);
            }

            // Get image files sorted numerically
            List<Path> imageFiles = Files.list(imagesPath)
                    .filter(f -> f.toString().matches(".*\\.(jpg|jpeg|png|gif)$"))
                    .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getFileName().toString().replaceAll("\\D+", ""))))
                    .collect(Collectors.toList());

            // Create filelist.txt for ffmpeg
            String fileListContent = imageFiles.stream()
                    .map(f -> "file '" + f.toAbsolutePath().toString().replace("\\", "/") + "'")
                    .collect(Collectors.joining("\n"));

            Files.write(fileListPath, fileListContent.getBytes());
            // FFmpeg command
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-r", "1/5",
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileListPath.toString(),
                    "-i", audioPath.toString(),
                    "-vf", "scale=-2:720",
                    "-c:v", "libx264",
                    "-c:a", "aac",
                    "-b:a", "192k",
                    "-pix_fmt", "yuv420p",
                    "-movflags", "+faststart",
                    outputVideoPath.toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Log FFmpeg output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg: " + line);
                }
            }

            int exitCode = process.waitFor();
            Files.deleteIfExists(fileListPath);

            if (exitCode == 0) {
                // Cleanup audio & images
                Files.deleteIfExists(audioPath);
                for (Path file : imageFiles) {
                    Files.deleteIfExists(file);
                }
                videoCounter++;
                return ResponseEntity.ok("Video generated successfully: " + outputVideoPath);
            } else {
                return ResponseEntity.status(500).body("Failed to generate video. Exit code: " + exitCode);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
