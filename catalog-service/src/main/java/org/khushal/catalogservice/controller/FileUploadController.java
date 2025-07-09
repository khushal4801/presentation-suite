package org.khushal.catalogservice.controller;

import org.khushal.catalogservice.model.Category;
import org.khushal.catalogservice.repository.CategoryRepository;
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
}
