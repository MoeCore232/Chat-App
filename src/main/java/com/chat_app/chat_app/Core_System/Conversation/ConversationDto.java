package com.chat_app.chat_app.Core_System.Conversation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationDto {

    public record CreateConversation (
            @NotNull(message = "is required")
            UUID user1Id,
            @NotNull(message = "is required")
            UUID user2Id
    ) {}

    public record ConversationResponse (
            UUID id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
