package com.example.demo.controllers;

import com.example.demo.dtos.ChatMessage;
import com.example.demo.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        String senderId = chatMessage.getSenderId();
        String recipientId = chatMessage.getTo();

        // 1. Handle Private Admin Reply
        if (recipientId != null && !recipientId.isEmpty()) {
            // Send to recipient
            messagingTemplate.convertAndSend("/topic/user." + recipientId, chatMessage);
            // Send back to sender so they see it in their chat history
            messagingTemplate.convertAndSend("/topic/user." + senderId, chatMessage);
            return;
        }

        // 2. Handle Admin/Human Handover Request
        boolean needsAdmin = chatMessage.getContent().toLowerCase().contains("admin")
                || chatMessage.getContent().toLowerCase().contains("human");

        if (needsAdmin) {
            // Send to admin topic
            messagingTemplate.convertAndSend("/topic/admin", chatMessage);
            // Send back to client on their private topic
            if (senderId != null) {
                messagingTemplate.convertAndSend("/topic/user." + senderId, chatMessage);
            }

            // Generate and send system response
            ChatMessage systemResponse = new ChatMessage("System",
                    "An administrator has been notified and will join the chat shortly.",
                    ChatMessage.MessageType.CHAT);

            if (senderId != null) {
                messagingTemplate.convertAndSend("/topic/user." + senderId, systemResponse);
            }
            messagingTemplate.convertAndSend("/topic/admin", systemResponse);
            return;
        }

        // 3. Regular Public Chat / AI Flow
        // Broadcast user message to public
        messagingTemplate.convertAndSend("/topic/public", chatMessage);

        // Process message through Rules/AI
        ChatMessage response = chatService.processMessage(chatMessage);
        if (response != null) {
            messagingTemplate.convertAndSend("/topic/public", response);
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
