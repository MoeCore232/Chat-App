package com.chat_app.chat_app.Shared.ErrorHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomResponseException extends RuntimeException {

    private String message;
    private int code;

    public static CustomResponseException idIsNotFound(UUID id) {
        return new CustomResponseException("Error: ID (" + id + ") is not found!", 404);
    }

    public static CustomResponseException publicError (String message, int code) {
        return new CustomResponseException(message, code);
    }

    public static CustomResponseException unExpectedErrorOccurred () {
        return new CustomResponseException("An unexpected error occurred. please try again later", 500);
    }

    public static CustomResponseException badCredentials () {
        return new CustomResponseException("Bad credentials!", 403);
    }

    public static CustomResponseException duplicateItem (String item) {
        return new CustomResponseException("This " + item + " already exists", 400);
    }

    public static CustomResponseException userIsNotParticipant () {
        return new CustomResponseException("User is not a participant!", 400);
    }
}
