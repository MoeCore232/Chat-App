package com.chat_app.chat_app.Core_System.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByUsername (String username);

    Optional<User> findByEmail (String email);

    List<User> findByUsernameContainingIgnoreCaseAndUsernameNot (
            String username, String currentUsername
    );

    @Query("""
    SELECT cp.user
    FROM ConversationParticipant cp
    WHERE cp.conversation.id = :conversationId
    AND cp.user.id <> :userId
    """)
    Optional<User> findOtherUser(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId
    );
}
