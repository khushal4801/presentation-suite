package org.khushal.catalogservice.controller;

import org.khushal.catalogservice.model.Category;
import org.khushal.catalogservice.repository.CategoryRepository;
import org.khushal.catalogservice.service.TtsService;
import org.khushal.catalogservice.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/allCategories")
public class FileUploadController {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TtsService ttsService;
    
    @Autowired
    private VideoService videoService;
    
    /**
     * Get all categories - matches Node.js endpoint GET /allCategories
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting categories: " + e.getMessage());
        }
    }

    @PostMapping("/{categoryId}/folders/{folderName}/upload")
    public ResponseEntity<?> uploadFiles(@PathVariable String categoryId,
                                         @PathVariable String folderName,
                                         @RequestParam("files") MultipartFile[] files) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId); // Fetch the category by ID

        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }

        Category category = categoryOpt.get(); // Get the category object

        // Base path: public/images/<category-name>/<folderName>
        Path folderPath = Paths.get("public", "images", category.getName(), folderName);

        try {
            Files.createDirectories(folderPath); // create if doesn't exist

            List<String> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();

                if (!originalName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                    return ResponseEntity.badRequest().body("Only image files (.jpg, .jpeg, .png, .gif) are allowed");
                }

                String filename = System.currentTimeMillis() + "-" + UUID.randomUUID() +
                        originalName.substring(originalName.lastIndexOf("."));

                Path destination = folderPath.resolve(filename);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                uploadedFiles.add(filename);
            }

            return ResponseEntity.ok(uploadedFiles.size() + " file(s) uploaded successfully: " + uploadedFiles);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Text-to-Speech endpoint - Generate MP3 file from text and save to folder
     * Matches Node.js endpoint: POST /allCategories/:categoryId/folders/:folderName/tts
     */
    @PostMapping("/{categoryId}/folders/{folderName}/tts")
    public ResponseEntity<?> generateTTS(@PathVariable String categoryId,
                                        @PathVariable String folderName,
                                        @RequestBody Map<String, String> requestBody) {
        String text = requestBody.get("text");
        
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body("Text is required");
        }
        
        // Check if category exists
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        
        Category category = categoryOpt.get();
        
        // Check if folder exists
        Path folderPath = Paths.get("public", "images", category.getName(), folderName);
        if (!Files.exists(folderPath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found");
        }
        
        try {
            // Generate MP3 file and save to folder
            Path audioPath = folderPath.resolve("audio.mp3");
            String savedPath = ttsService.generateSpeech(text, audioPath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Saved MP3 file: " + savedPath);
            response.put("fileName", "audio.mp3");
            response.put("filePath", savedPath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating MP3 file: " + e.getMessage());
        }
    }

    /**
     * Generate video from images and audio - matches Node.js endpoint
     * POST /allCategories/:categoryId/folders/:folderName/generateVideo
     */
    @PostMapping("/{categoryId}/folders/{folderName}/generateVideo")
    public ResponseEntity<?> generateVideo(@PathVariable String categoryId,
                                          @PathVariable String folderName) {
        // Check if category exists
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        
        Category category = categoryOpt.get();
        
        // Check if folder exists
        Path folderPath = Paths.get("public", "images", category.getName(), folderName);
        if (!Files.exists(folderPath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found");
        }
        
        // Check if audio file exists
        Path audioPath = folderPath.resolve("audio.mp3");
        if (!Files.exists(audioPath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Audio file not found");
        }
        
        try {
            // Call video-service to generate video
            // Using Node.js defaults: fps 1/5 (which is 5 seconds per image), height 720
            String audioPathStr = audioPath.toString().replace("\\", "/");
            ResponseEntity<?> result = videoService.generateVideo(
                    category.getName(), 
                    folderName, 
                    audioPathStr,
                    5,  // secondsPerImage (Node.js uses 1/5 fps = 5 seconds per image)
                    720, // height
                    "uploads", // outputDir
                    true, // cleanupImages (Node.js deletes images after generation)
                    true  // cleanupAudio (Node.js deletes audio after generation)
            );
            
            // Parse response and increment videoCounter
            if (result.getStatusCode().is2xxSuccessful() && result.getBody() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) result.getBody();
                String outputPath = (String) responseBody.get("outputPath");
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Video generated successfully: " + outputPath);
                response.put("outputPath", outputPath);
                return ResponseEntity.ok(response);
            }
            
            return result;
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating video: " + e.getMessage());
        }
    }
}
