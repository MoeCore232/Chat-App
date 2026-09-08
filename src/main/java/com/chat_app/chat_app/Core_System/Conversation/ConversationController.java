package com.chat_app.chat_app.Core_System.Conversation;

import com.chat_app.chat_app.Shared.ErrorHandling.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/get-all-conversations")
    public ResponseEntity<GlobalResponse<List<ConversationDto.ConversationResponse>>> getAllConversations () {
        List<ConversationDto.ConversationResponse> conversations = conversationService.getAllConversation();
        return new ResponseEntity<>(new GlobalResponse<>(conversations), HttpStatus.OK);
    }

    @PostMapping("/create-conversation")
    public ResponseEntity<GlobalResponse<ConversationDto.ConversationResponse>> createConversations (@RequestBody @Valid ConversationDto.CreateConversation createConversation) {
        ConversationDto.ConversationResponse response = conversationService.createConversation(createConversation);
        return new ResponseEntity<>(new GlobalResponse<>(response), HttpStatus.OK);
    }
}
