package com.chat_app.chat_app.Core_System.ConfermationCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationCodeRepo extends JpaRepository<ConfirmationCode, UUID> {
    Optional<ConfirmationCode> findByUserId (UUID userId);
}
