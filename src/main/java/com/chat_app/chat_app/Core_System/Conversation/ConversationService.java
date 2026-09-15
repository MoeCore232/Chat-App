package com.chat_app.chat_app.Core_System.Conversation;

import com.chat_app.chat_app.Core_System.Message.Message;
import com.chat_app.chat_app.Core_System.Message.MessageRepo;
import com.chat_app.chat_app.Core_System.User.User;
import com.chat_app.chat_app.Core_System.User.UserRepo;
import com.chat_app.chat_app.Shared.ErrorHandling.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    ConversationParticipantRepo conversationParticipantRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MessageRepo messageRepo;

    public List<ConversationDto.ConversationResponse> getAllUserConversations(UUID userId) {
        return conversationParticipantRepo.findByUserId(userId)
                .stream()
                .map(participant -> {

                    Conversation conversation = participant.getConversation();

                    int unreadCount = participant.getUnreadCount();

                    String lastMessage = messageRepo.findTopByConversationIdOrderByCreatedAtDesc(
                            conversation.getId()
                    ).map(Message::getContent).orElse("No messages yet.");

                    String otherUsername = conversation.getParticipants()
                            .stream()
                            .map(ConversationParticipant::getUser)
                            .filter(user -> !user.getId().equals(userId))
                            .map(User::getName)
                            .findFirst()
                            .orElse("Unknown");

                    return new ConversationDto.ConversationResponse(
                            conversation.getId(),
                            otherUsername,
                            conversation.getCreatedAt(),
                            conversation.getUpdatedAt(),
                            unreadCount,
                            lastMessage
                    );
                })
                .toList();
    }

    public ConversationDto.ConversationResponse createConversation(
            ConversationDto.CreateConversation createConversation
    ) {

        User findUser1 = userRepo.findById(createConversation.user1Id())
                .orElseThrow(() ->
                        CustomResponseException.idIsNotFound(
                                createConversation.user1Id()
                        )
                );

        User findUser2 = userRepo.findById(createConversation.user2Id())
                .orElseThrow(() ->
                        CustomResponseException.idIsNotFound(
                                createConversation.user2Id()
                        )
                );

        Optional<Conversation> findConversation =
                conversationParticipantRepo.findConversationBetween(
                        findUser1.getId(),
                        findUser2.getId()
                );

        if (findConversation.isPresent()) {

            Conversation conversation = findConversation.get();

            int unreadCount = conversationParticipantRepo
                    .findByConversationIdAndUserId(
                            conversation.getId(),
                            findUser1.getId()
                    )
                    .map(ConversationParticipant::getUnreadCount)
                    .orElse(0);

            return new ConversationDto.ConversationResponse(
                    conversation.getId(),
                    findUser2.getName(),
                    conversation.getCreatedAt(),
                    conversation.getUpdatedAt(),
                    unreadCount,
                    "No messages yet."
            );
        }

        Conversation conversation = Conversation.create();

        ConversationParticipant participant1 =
                ConversationParticipant.create(
                        conversation,
                        findUser1
                );

        ConversationParticipant participant2 =
                ConversationParticipant.create(
                        conversation,
                        findUser2
                );

        conversation.addParticipants(participant1);
        conversation.addParticipants(participant2);

        conversationRepo.save(conversation);

        conversationParticipantRepo.save(participant1);
        conversationParticipantRepo.save(participant2);

        String lastMessage = messageRepo.findTopByConversationIdOrderByCreatedAtDesc(
                conversation.getId()
        ).map(Message::getContent).orElse("No messages yet.");

        return new ConversationDto.ConversationResponse(
                conversation.getId(),
                findUser2.getName(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                0,
                lastMessage
        );
    }

    public void markConversationAsRead(UUID conversationId, UUID userId) {

        ConversationParticipant participant = conversationParticipantRepo
                        .findByConversationIdAndUserId(
                                conversationId,
                                userId
                        ).orElseThrow(() -> CustomResponseException.idIsNotFound(userId));

        participant.setUnreadCount(0);

        conversationParticipantRepo.save(participant);
    }
}
