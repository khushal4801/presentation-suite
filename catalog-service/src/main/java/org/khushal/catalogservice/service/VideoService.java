package org.khushal.catalogservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class VideoService {
    
    private final WebClient webClient;
    
    @Value("${video.service.url:http://localhost:8082}")
    private String videoServiceUrl;
    
    @Autowired
    public VideoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    /**
     * Generate video from images and audio using video-service
     * @param category Category name
     * @param folder Folder name
     * @param audioPath Path to audio file
     * @param secondsPerImage Seconds per image (default: 5)
     * @param height Video height (default: 720)
     * @param outputDir Output directory (default: uploads)
     * @param cleanupImages Whether to cleanup images after generation
     * @param cleanupAudio Whether to cleanup audio after generation
     * @return Response from video-service
     */
    public ResponseEntity<?> generateVideo(String category, String folder, String audioPath,
                                          int secondsPerImage, int height, String outputDir,
                                          boolean cleanupImages, boolean cleanupAudio) {
        try {
            // Build URI with query parameters
            String uri = String.format("%s/api/video/merge?category=%s&folder=%s&audioPath=%s&secondsPerImage=%d&height=%d&outputDir=%s&cleanupImages=%s&cleanupAudio=%s",
                    videoServiceUrl, category, folder, audioPath, secondsPerImage, height, outputDir, cleanupImages, cleanupAudio);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) webClient.post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error calling video-service: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

