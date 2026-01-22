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

        if (recipientId != null && !recipientId.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/user." + recipientId, chatMessage);
            messagingTemplate.convertAndSend("/topic/user." + senderId, chatMessage);
            return;
        }

        boolean needsAdmin = chatMessage.getContent().toLowerCase().contains("admin")
                || chatMessage.getContent().toLowerCase().contains("human");

        if (needsAdmin) {
            messagingTemplate.convertAndSend("/topic/admin", chatMessage);
            if (senderId != null) {
                messagingTemplate.convertAndSend("/topic/user." + senderId, chatMessage);
            }

            ChatMessage systemResponse = new ChatMessage("System",
                    "An administrator has been notified and will join the chat shortly.",
                    ChatMessage.MessageType.CHAT);

            if (senderId != null) {
                messagingTemplate.convertAndSend("/topic/user." + senderId, systemResponse);
            }
            messagingTemplate.convertAndSend("/topic/admin", systemResponse);
            return;
        }

        messagingTemplate.convertAndSend("/topic/public", chatMessage);

        ChatMessage response = chatService.processMessage(chatMessage);
        if (response != null) {
            messagingTemplate.convertAndSend("/topic/public", response);
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
