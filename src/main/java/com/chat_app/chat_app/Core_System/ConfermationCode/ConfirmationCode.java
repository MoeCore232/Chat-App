package com.chat_app.chat_app.Core_System.ConfermationCode;

import com.chat_app.chat_app.Core_System.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "confirmation_codes")
public class ConfirmationCode {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_for", nullable = false)
    private User user;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "is_success", nullable = false)
    private boolean isSuccess;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static ConfirmationCode create (User user, String code) {
        ConfirmationCode confirmationCode = new ConfirmationCode();

        confirmationCode.user = user;
        confirmationCode.code = code;
        confirmationCode.expiredAt = LocalDateTime.now().plusMinutes(1);
        confirmationCode.isSuccess = false;

        return confirmationCode;
    }

    public void successResult () {
        this.isSuccess = true;
    }

    public void resendCode (String code) {
        this.code = code;
        LocalDateTime newTime = LocalDateTime.now().plusMinutes(10);
        this.expiredAt = newTime;
    }
}
