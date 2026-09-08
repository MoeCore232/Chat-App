package com.chat_app.chat_app.Core_System.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public enum Roles {
        USER_ROLE, ADMIN_ROLE
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private Roles role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static User createUser (UserDto.CreateUser createUser, String password) {
        User user = new User();
        user.name = createUser.name();
        user.username = createUser.username();
        user.email = createUser.email();
        user.password = password;
        user.role = Roles.ADMIN_ROLE;
        return user;
    }

    public static User updateUser (User user, UserDto.UpdateUser updateUser) {
        user.name = updateUser.name();
        user.username = updateUser.username();
        return user;
    }
}
