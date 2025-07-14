package org.khushal.ttsservice.controller;

import org.khushal.ttsservice.service.TtsClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tts-client")
public class TtsClientController {

    private final TtsClientService service;

    public TtsClientController(TtsClientService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateTTS(@RequestBody String text) {
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body("Text cannot be null or empty");
        }
        System.out.println("Received text for TTS: " + text);
        // Call the service to generate speech
        return service.generateSpeech(text);
    }
}