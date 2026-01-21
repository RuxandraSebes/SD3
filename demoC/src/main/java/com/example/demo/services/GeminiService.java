package com.example.demo.services;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    private Client client;

    @PostConstruct
    public void init() {
        try {
            if (apiKey == null || apiKey.equals("N/A") || apiKey.isEmpty()) {
                log.warn("Gemini API key is not configured.");
                return;
            }
            this.client = Client.builder()
                    .apiKey(apiKey)
                    .build();
            log.info("Gemini Client initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize Gemini Client: {}", e.getMessage(), e);
        }
    }

    public String generateResponse(String userMessage) {
        try {
            if (client == null) {
                log.error("Gemini Client is not initialized.");
                return "AI system is currently unavailable.";
            }

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    buildPrompt(userMessage),
                    null);

            if (response != null && response.text() != null) {
                return response.text();
            }
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
        }

        return "I'm sorry, I couldn't process your request.";
    }

    private String buildPrompt(String userMessage) {
        return "You are a helpful customer support assistant" +
                "Answer the following user question concisely and professionally:\n\n" +
                "User: " + userMessage + "\n\n";
    }
}
