package com.chat_app.chat_app.Core_System.Message;

import com.chat_app.chat_app.Shared.ErrorHandling.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/get-all-messages/{conversationId}")
    public ResponseEntity<GlobalResponse<List<MessageDto.MessageResponse>>> getAllMessages (@PathVariable UUID conversationId) {
        List<MessageDto.MessageResponse> messages = messageService.getAllMessages(conversationId);
        return new ResponseEntity<>(new GlobalResponse<>(messages), HttpStatus.OK);
    }

    @PostMapping("/create-message")
    public ResponseEntity<GlobalResponse<MessageDto.MessageResponse>> createMessage (@RequestBody @Valid MessageDto.CreateMessage createMessage) {
        MessageDto.MessageResponse response = messageService.createMessage(createMessage);
        return new ResponseEntity<>(new GlobalResponse<>(response), HttpStatus.OK);
    }

    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<GlobalResponse<String>> deleteMessage (@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return new ResponseEntity<>(new GlobalResponse<>("Message deleted successful!"), HttpStatus.OK);
    }
}
