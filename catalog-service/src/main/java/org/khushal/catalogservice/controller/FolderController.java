package org.khushal.catalogservice.controller;


import org.khushal.catalogservice.model.Category;
import org.khushal.catalogservice.repository.CategoryRepository;
import org.khushal.catalogservice.model.FolderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@RestController
@RequestMapping("/allCategories")
public class FolderController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/{id}/folders")
    public ResponseEntity<String> createFolder(@PathVariable String id, @RequestBody FolderRequest request) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }

        Category category = categoryOpt.get();
        String folderName = request.getName();

        // Path: public/images/<category-name>/<folder-name>
        Path categoryDir = Paths.get("public", "images", category.getName());
        Path folderPath = categoryDir.resolve(folderName);

        try {
            if (!Files.exists(categoryDir)) {
                Files.createDirectories(categoryDir); // Create category folder
            }

            if (Files.exists(folderPath)) {
                return ResponseEntity.badRequest().body("Folder already exists");
            }

            Files.createDirectory(folderPath); // Create the new folder
            return ResponseEntity.ok("Folder created: " + folderPath.toString());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating folder: " + e.getMessage());
        }
    }
}
