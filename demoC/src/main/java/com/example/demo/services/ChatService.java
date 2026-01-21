package com.example.demo.services;

import com.example.demo.dtos.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@Service
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private final Map<String, String> rules = new HashMap<>();

    @Autowired
    private GeminiService geminiService;

    public ChatService() {
        initializeRules();
    }

    private void initializeRules() {
        rules.put("hours", "Our support hours are 9 AM to 5 PM, Monday to Friday.");
        rules.put("location", "We are located at 123 Smart City Blvd.");
        rules.put("price", "Smart devices start at $99. Check the dashboard for details.");
        rules.put("consumption", "You can view your energy consumption in the 'My Dashboard' section.");
        rules.put("register", "To register a new device, please contact an admin.");
        rules.put("login", "Use your username and password to log in. Reset password via email if needed.");
        rules.put("password", "To reset your password, click 'Forgot Password' on the login page.");
        rules.put("admin", "I will forward your request to an administrator.");
        rules.put("human", "Connecting you to a human agent...");
        rules.put("help", "I can help with hours, location, prices, consumption, and account issues.");
    }

    public ChatMessage processMessage(ChatMessage message) {
        String content = message.getContent().toLowerCase();

        // 1. Check for Admin/Human request
        if (content.contains("admin") || content.contains("human")) {
            LOGGER.info("User {} requested admin intervention.", message.getSender());
            return new ChatMessage("System", "An administrator has been notified and will join the chat shortly.",
                    ChatMessage.MessageType.CHAT);
        }

        // 2. Check Rules
        for (Map.Entry<String, String> entry : rules.entrySet()) {
            if (content.contains(entry.getKey())) {
                return new ChatMessage("AutoBot", entry.getValue(), ChatMessage.MessageType.CHAT);
            }
        }

        // 3. Fallback to AI
        String aiResponse = geminiService.generateResponse(content);
        return new ChatMessage("AI-Assistant", aiResponse, ChatMessage.MessageType.CHAT);
    }
}
