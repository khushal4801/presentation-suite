package org.khushal.ttsservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.*;
import java.util.*;

@Service
public class TtsClientService {
    private final WebClient webClient;

    public TtsClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8001/").build();
    }

    public ResponseEntity<?> generateSpeech(String text) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);

            byte[] audioBytes = webClient.post()
                    .uri("/generate-tts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            // Save to local folder
            Path savePath = Paths.get("uploads", "audio", "output.mp3");
            // Ensure the parent directory exists
            if (audioBytes == null || audioBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("No audio data received from TTS service");
            }

            //already above audioByte save output.mp3 file now i want to just save that file in uploads/audio folder

            Files.createDirectories(savePath.getParent()); // Ensure the directory exists
            Files.write(savePath, audioBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Return the path where the file is saved
            return ResponseEntity.ok("Audio file saved to: " + savePath.toString());


        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating speech: " + e.getMessage());
        }
    }
}
