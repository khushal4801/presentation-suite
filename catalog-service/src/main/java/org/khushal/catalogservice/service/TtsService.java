package org.khushal.catalogservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

@Service
public class TtsService {
    
    private final WebClient webClient;
    
    // FastAPI service URL (the Python FastAPI service that tts-service calls)
    @Value("${tts.fastapi.url:http://127.0.0.1:8001}")
    private String fastApiUrl;
    
    @Autowired
    public TtsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    /**
     * Generate speech from text using FastAPI TTS service and save to specified path
     * This calls the FastAPI service directly (same as tts-service does internally)
     * @param text The text to convert to speech
     * @param savePath The path where the audio file should be saved
     * @return The path where the file was saved
     * @throws Exception If TTS generation fails
     */
    public String generateSpeech(String text, Path savePath) throws Exception {
        // Prepare request body matching FastAPI TTSRequest format
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", text);
        
        // Call FastAPI service directly (same endpoint that tts-service uses)
        byte[] audioBytes = webClient.post()
                .uri(fastApiUrl + "/generate-tts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        
        if (audioBytes == null || audioBytes.length == 0) {
            throw new Exception("No audio data received from TTS service");
        }
        
        // Ensure the parent directory exists
        Files.createDirectories(savePath.getParent());
        Files.write(savePath, audioBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        return savePath.toString();
    }
}

