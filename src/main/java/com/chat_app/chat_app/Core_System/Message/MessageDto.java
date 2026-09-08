package com.chat_app.chat_app.Core_System.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDto {

    public record CreateMessage (
            @NotNull(message = "is required")
            UUID conversationId,
            @NotNull(message = "is required")
            UUID senderId,
            @NotBlank(message = "is required")
            String content
    ) {}

    public record MessageResponse (
            UUID id,
            UUID senderId,
            String content,
            LocalDateTime createdAt
    ) {}
}
