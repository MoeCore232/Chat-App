package com.chat_app.chat_app.Core_System.User;

import com.chat_app.chat_app.Shared.Config.JwtHelper;
import com.chat_app.chat_app.Shared.ErrorHandling.CustomResponseException;
import jakarta.transaction.Transactional;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    public List<User> getAllUsers () {
        return userRepo.findAll();
    }

    public User getUserById (UUID userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));
        return findUser;
    }

    @Transactional
    public UserDto.AuthResponse createUser (UserDto.CreateUser createUser) {

        Optional<User> isUsernameFound = userRepo.findByUsername(createUser.username());
        if (isUsernameFound.isPresent()) {
            throw CustomResponseException.duplicateItem("username");
        }

        Optional<User> isEmailFound = userRepo.findByEmail(createUser.email());
        if (isEmailFound.isPresent()) {
            throw CustomResponseException.duplicateItem("email");
        }

        String password = passwordEncoder.encode(createUser.password());
        User user = User.createUser(createUser, password);

        String token = jwtHelper.generateToken(user);
        userRepo.save(user);

        return new UserDto.AuthResponse(token, "User created successful!");
    }

    @Transactional
    public UserDto.AuthResponse sighIn (UserDto.SighIn sighIn) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        sighIn.username(),
                        sighIn.password()
                ));

        User findUser = userRepo.findByUsername(sighIn.username())
                .orElseThrow(() -> CustomResponseException.badCredentials());

        String token = jwtHelper.generateToken(findUser);
        return new UserDto.AuthResponse(token, "Login Successful!");
    }

    public void updateUser (UUID userId, UserDto.UpdateUser updateUser) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));
        User user = User.updateUser(findUser, updateUser);
        userRepo.save(user);
    }

    public void deleteUser (UUID userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(()-> CustomResponseException.idIsNotFound(userId));
        userRepo.deleteById(findUser.getId());
    }
}
