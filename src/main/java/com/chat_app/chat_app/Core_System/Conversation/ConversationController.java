package com.chat_app.chat_app.Core_System.Conversation;

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
@RequestMapping("/api/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/get-all-user-conversations/{userId}")
    public ResponseEntity<GlobalResponse<List<ConversationDto.ConversationResponse>>> getAllUserConversations (@PathVariable UUID userId) {
        List<ConversationDto.ConversationResponse> conversations = conversationService.getAllUserConversations(userId);
        return new ResponseEntity<>(new GlobalResponse<>(conversations), HttpStatus.OK);
    }

    @PostMapping("/create-conversation")
    public ResponseEntity<GlobalResponse<ConversationDto.ConversationResponse>> createConversations (@RequestBody @Valid ConversationDto.CreateConversation createConversation) {
        ConversationDto.ConversationResponse response = conversationService.createConversation(createConversation);
        return new ResponseEntity<>(new GlobalResponse<>(response), HttpStatus.OK);
    }

    @PutMapping("/mark-as-read/{conversationId}/{userId}")
    public ResponseEntity<?> markAsRead (@PathVariable UUID conversationId, @PathVariable UUID userId) {
        conversationService.markConversationAsRead(conversationId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
