package com.chat_app.chat_app.Core_System.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class UserDto {

    public record CreateUser (
            @NotBlank(message = "is required")
            String name,
            @NotBlank(message = "is required")
            String username,
            @Email(message = "is required")
            String email,
            @NotBlank(message = "is required")
            String password
    ) {}

    public record SighIn (
            @NotBlank(message = "is required")
            String username,
            @NotBlank(message = "is required")
            String password
    ) {}

    public record UpdateUser (
            @NotBlank(message = "is required")
            String name,
            @NotBlank(message = "is required")
            String username
    ) {}

    public record AuthResponse (
            UUID id,
            String token,
            String message
    ) {}

    public record SearchResponse (
            UUID id,
            String name,
            String username
    ) {}

    public record UserChatInfoResponse (
            String name,
            String username
    ) {}

    public record ConfirmationCode (
            String code
    ) {}

    public record CreateAccountResponse (
            UUID id
    ) {}

    public record ExpoPushTokenRequest (
            String token
    ) {}
}
