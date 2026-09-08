package com.chat_app.chat_app.Core_System.Message;

import com.chat_app.chat_app.Core_System.Conversation.Conversation;
import com.chat_app.chat_app.Core_System.Conversation.ConversationParticipantRepo;
import com.chat_app.chat_app.Core_System.Conversation.ConversationRepo;
import com.chat_app.chat_app.Core_System.User.User;
import com.chat_app.chat_app.Core_System.User.UserRepo;
import com.chat_app.chat_app.Shared.ErrorHandling.CustomResponseException;
import org.hibernate.query.sqm.tree.expression.Conversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private ConversationParticipantRepo conversationParticipantRepo;


    public List<MessageDto.MessageResponse> getAllMessages (UUID conversationId) {

       if (!conversationRepo.existsById(conversationId)) {
           throw CustomResponseException.idIsNotFound(conversationId);
       }

       return messageRepo.findByConversationIdOrderByCreatedAtAsc(conversationId)
               .stream()
               .map(message -> new MessageDto.MessageResponse(
                       message.getId(),
                       message.getSender().getId(),
                       message.getContent(),
                       message.getCreatedAt()
               )).toList();
    }

    public MessageDto.MessageResponse createMessage (MessageDto.CreateMessage createMessage) {
        Conversation findConversion = conversationRepo.findById(createMessage.conversationId())
                .orElseThrow(() -> CustomResponseException.idIsNotFound(createMessage.conversationId()));

        User findUser = userRepo.findById(createMessage.senderId())
                .orElseThrow(() -> CustomResponseException.idIsNotFound(createMessage.senderId()));

        boolean isParticipant = conversationParticipantRepo.existsByConversationIdAndUserId(
                findConversion.getId(),
                findUser.getId()
        );

        if (!isParticipant) {
            throw CustomResponseException.userIsNotParticipant();
        }

        Message message = Message.create(findConversion, findUser, createMessage.content());

        Message savedMessage = messageRepo.save(message);

        return new MessageDto.MessageResponse(
                savedMessage.getId(),
                savedMessage.getSender().getId(),
                savedMessage.getContent(),
                savedMessage.getCreatedAt()
        );
    }

    public void deleteMessage (UUID messageId) {
        Message findMessage = messageRepo.findById(messageId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(messageId));
        messageRepo.deleteById(findMessage.getId());
    }
}
