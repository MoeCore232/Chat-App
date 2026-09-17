package com.chat_app.chat_app.Core_System.Conversation;

import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationParticipantRepo extends JpaRepository<ConversationParticipant, UUID> {
    @Query("""
            SELECT cp1.conversation
            FROM ConversationParticipant cp1
            JOIN ConversationParticipant cp2
                ON cp1.conversation = cp2.conversation
            WHERE cp1.user.id = :user1Id
              AND cp2.user.id = :user2Id
            """)
    Optional<Conversation> findConversationBetween(
            UUID user1Id,
            UUID user2Id
    );

    boolean existsByConversationIdAndUserId (UUID conversationId, UUID userId);

    List<ConversationParticipant> findByUserId (UUID userId);

    Optional<ConversationParticipant> findByConversationIdAndUserId (
            UUID conversationId,
            UUID userId
    );

    @Query("""
        SELECT cp
        FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
          AND cp.user.id <> :userId
        """)
    Optional<ConversationParticipant> findOtherParticipant(
            UUID conversationId,
            UUID userId
    );

    List<ConversationParticipant> findByConversationId (UUID conversationId);
}
