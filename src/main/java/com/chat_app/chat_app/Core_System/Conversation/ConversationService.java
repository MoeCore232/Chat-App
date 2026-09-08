package com.chat_app.chat_app.Core_System.Conversation;

import com.chat_app.chat_app.Core_System.User.User;
import com.chat_app.chat_app.Core_System.User.UserRepo;
import com.chat_app.chat_app.Shared.ErrorHandling.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepo conversationRepo;

    @Autowired
    ConversationParticipantRepo conversationParticipantRepo;

    @Autowired
    private UserRepo userRepo;

    public List<ConversationDto.ConversationResponse> getAllConversation () {
        return conversationRepo
                .findAll()
                .stream()
                .map(conversation -> new ConversationDto.ConversationResponse(
                        conversation.getId(),
                        conversation.getCreatedAt(),
                        conversation.getUpdatedAt()
                )).toList();
    }

    public ConversationDto.ConversationResponse createConversation (ConversationDto.CreateConversation createConversation) {
        User findUser1 = userRepo.findById(createConversation.user1Id())
                .orElseThrow(() -> CustomResponseException.idIsNotFound(createConversation.user1Id()));

        User findUser2 = userRepo.findById(createConversation.user2Id())
                .orElseThrow(() -> CustomResponseException.idIsNotFound(createConversation.user2Id()));

        Optional<Conversation> findConversation = conversationParticipantRepo.findConversationBetween(findUser1.getId(), findUser2.getId());

        if (findConversation.isPresent()) {
            Conversation conversation = findConversation.get();
            return new ConversationDto.ConversationResponse(
                    conversation.getId(),
                    conversation.getCreatedAt(),
                    conversation.getUpdatedAt()
            );
        }

        Conversation conversation = Conversation.create();

        ConversationParticipant participant1 = ConversationParticipant.create(conversation, findUser1);
        ConversationParticipant participant2 = ConversationParticipant.create(conversation, findUser2);

        conversation.addParticipants(participant1);
        conversation.addParticipants(participant2);

        conversationRepo.save(conversation);

        conversationParticipantRepo.save(participant1);
        conversationParticipantRepo.save(participant2);

        return new ConversationDto.ConversationResponse(conversation.getId(), conversation.getCreatedAt(), conversation.getUpdatedAt());
    }
}
