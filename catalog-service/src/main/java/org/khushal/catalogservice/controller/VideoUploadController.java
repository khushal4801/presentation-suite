package org.khushal.catalogservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class VideoUploadController {

    private final Path uploadDir = Paths.get("uploads");
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
}
