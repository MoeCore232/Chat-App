package com.chat_app.chat_app.Core_System.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage (MessageDto.CreateMessage createMessage) {
        System.out.println("Received: " + createMessage.content());

        MessageDto.MessageResponse response = messageService.createMessage(createMessage);

        simpMessagingTemplate.convertAndSend(
                "/topic/conversations" + createMessage.conversationId(), response
        );
    }
}
