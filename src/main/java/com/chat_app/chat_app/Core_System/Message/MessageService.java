package com.chat_app.chat_app.Core_System.Message;

import com.chat_app.chat_app.Core_System.Conversation.Conversation;
import com.chat_app.chat_app.Core_System.Conversation.ConversationParticipantRepo;
import com.chat_app.chat_app.Core_System.Conversation.ConversationRepo;
import com.chat_app.chat_app.Core_System.User.User;
import com.chat_app.chat_app.Core_System.User.UserRepo;
import com.chat_app.chat_app.Shared.ErrorHandling.CustomResponseException;
import com.chat_app.chat_app.Shared.Notifications.NotificationsService;
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

    @Autowired
    private NotificationsService notificationService;


    public List<MessageDto.MessageResponse> getAllMessages (UUID conversationId) {

       if (!conversationRepo.existsById(conversationId)) {
           throw CustomResponseException.idIsNotFound(conversationId);
       }

       return messageRepo.findByConversationIdOrderByCreatedAtAsc(conversationId)
               .stream()
               .map(message -> new MessageDto.MessageResponse(
                       message.getId(),
                       message.getSender().getId(),
                       message.getSender().getName(),
                       message.getContent(),
                       message.getCreatedAt()
               )).toList();
    }

    public MessageDto.MessageResponse createMessage(MessageDto.CreateMessage createMessage) {

        Conversation findConversion = conversationRepo.findById(
                        createMessage.conversationId()
                )
                .orElseThrow(() ->
                        CustomResponseException.idIsNotFound(
                                createMessage.conversationId()
                        )
                );

        User findUser = userRepo.findById(
                        createMessage.senderId()
                )
                .orElseThrow(() ->
                        CustomResponseException.idIsNotFound(
                                createMessage.senderId()
                        )
                );

        boolean isParticipant =
                conversationParticipantRepo.existsByConversationIdAndUserId(
                        findConversion.getId(),
                        findUser.getId()
                );

        if (!isParticipant) {
            throw CustomResponseException.userIsNotParticipant();
        }

        Message message = Message.create(
                findConversion,
                findUser,
                createMessage.content()
        );

        Message savedMessage = messageRepo.save(message);

        conversationParticipantRepo
                .findOtherParticipant(
                        findConversion.getId(),
                        findUser.getId()
                )
                .ifPresent(participant -> {

                    participant.setUnreadCount(
                            participant.getUnreadCount() + 1
                    );

                    conversationParticipantRepo.save(participant);

                    String expoPushToken =
                            participant.getUser().getExpoPushToken();

                    if (expoPushToken != null && !expoPushToken.isBlank()) {

                        notificationService.sendNotification(
                                expoPushToken,
                                findUser.getName(),
                                savedMessage.getContent()
                        );
                    }
                });

        return new MessageDto.MessageResponse(
                savedMessage.getId(),
                savedMessage.getSender().getId(),
                findUser.getName(),
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
