package com.chat_app.chat_app.Core_System.Conversation;

import com.chat_app.chat_app.Core_System.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "conversation_participants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"conversation_id", "user_id"})
})
public class ConversationParticipant {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected ConversationParticipant () {

    }

    private ConversationParticipant (Conversation conversation, User user) {
        this.conversation = conversation;
        this.user = user;
    }

    public static ConversationParticipant create (Conversation conversation, User user) {
        return new ConversationParticipant(conversation, user);
    }
}
