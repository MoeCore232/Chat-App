package com.chat_app.chat_app.Shared.ErrorHandling;

import lombok.Getter;

import java.util.List;

@Getter
public class GlobalResponse<T> {

    private final String ERROR = "Error";
    private final String SUCCESSFUL = "Successful";

    private String status;
    private T data;
    private List<ErrorItem> errorItems;

    public record ErrorItem (String message) { }

    public GlobalResponse (T data) {
        this.status = SUCCESSFUL;
        this.data = data;
        this.errorItems = null;
    }

    public GlobalResponse (List<ErrorItem> errorItems) {
        this.status = ERROR;
        this.data = null;
        this.errorItems = errorItems;
    }
}
