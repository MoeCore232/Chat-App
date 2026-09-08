package com.chat_app.chat_app.Core_System.User;

import jakarta.validation.constraints.NotBlank;

public class UserDto {

    public record CreateUser (
            @NotBlank(message = "is required")
            String name,
            @NotBlank(message = "is required")
            String username,
            @NotBlank(message = "is required")
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
            String token,
            String message
    ) {}
}
