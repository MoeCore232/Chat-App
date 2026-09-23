package com.chat_app.chat_app.Core_System.User;

import com.chat_app.chat_app.Core_System.ConfermationCode.ConfirmationCode;
import com.chat_app.chat_app.Core_System.ConfermationCode.ConfirmationCodeRepo;
import com.chat_app.chat_app.Shared.Config.JwtHelper;
import com.chat_app.chat_app.Shared.ErrorHandling.CustomResponseException;
import com.chat_app.chat_app.Shared.Notifications.SendEmailService;
import com.chat_app.chat_app.Shared.Utils.GenerateRandomCode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.resend.core.exception.ResendException;

import java.time.LocalDateTime;
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

    @Autowired
    private ConfirmationCodeRepo confirmationCodeRepo;

    @Autowired
    private GenerateRandomCode generateRandomCode;

    @Autowired
    private SendEmailService sendEmailService;

    public List<User> getAllUsers () {
        return userRepo.findAll();
    }

    public User getUserById (UUID userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));

        return findUser;
    }

    @Transactional
    public UserDto.CreateAccountResponse createUser (UserDto.CreateUser createUser) {

        if (createUser.password().length() < 8) {
            throw CustomResponseException.publicError("كلمة المرور يجب ان تتكون من 8 احرف على الاقل.", 400);
        }

        Optional<User> isUsernameFound = userRepo.findByUsername(createUser.username());

        if (isUsernameFound.isPresent()) {
            throw CustomResponseException.duplicateItem("اسم المستخدم");
        }

        Optional<User> isEmailFound = userRepo.findByEmail(createUser.email());

        if (isEmailFound.isPresent()) {
            throw CustomResponseException.duplicateItem("البريد الالكتروني");
        }

        try {
            String password = passwordEncoder.encode(createUser.password());

            User user = User.createUser(createUser, password);

            userRepo.save(user);

            String code = generateRandomCode.generateRandomCode();

            System.out.println("Code wen sent: " + code);

            ConfirmationCode confirmationCode = ConfirmationCode.create(user, code);

            confirmationCodeRepo.save(confirmationCode);

            sendEmailService.confirmationMessage(user.getEmail(), code);

            return new UserDto.CreateAccountResponse(user.getId());
        } catch (MatchException e) {
            e.printStackTrace();
            System.out.println(e);
            throw e;
        }
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

        sendEmailService.welcomeMessage(findUser.getEmail());

        String token = jwtHelper.generateToken(findUser);

        return new UserDto.AuthResponse(findUser.getId(), token, "Login Successful!");
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

    public List<UserDto.SearchResponse> searchByNameOrUsername (String username, String currentUsername) {
        return userRepo.findByUsernameContainingIgnoreCaseAndUsernameNot(username, currentUsername)
                .stream()
                .map(user -> new UserDto.SearchResponse(
                        user.getId(),
                        user.getName(),
                        user.getUsername()
                )).toList();
    }

    public UserDto.UserChatInfoResponse userChatInfo (UUID conversationId, UUID userId) {
        User findUser = userRepo.findOtherUser(conversationId, userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));

        return new UserDto.UserChatInfoResponse(findUser.getName(), findUser.getUsername());
    }

    @org.springframework.transaction.annotation.Transactional
    public UserDto.AuthResponse verifyEmailCode (UUID userId, UserDto.ConfirmationCode confirmationCodeDto) {

        Optional<ConfirmationCode> findConfirmationCode = confirmationCodeRepo.findByUserId(
                userId
        );

        if (findConfirmationCode.isEmpty()) {
            throw CustomResponseException.idIsNotFound(userId);
        }

        ConfirmationCode confirmationCode = findConfirmationCode.get();

        if (!confirmationCodeDto.code().equals(confirmationCode.getCode())) {
            throw CustomResponseException.incorrectCode();
        }

        LocalDateTime now = LocalDateTime.now();

        if (!now.isBefore(confirmationCode.getExpiredAt())) {
            throw CustomResponseException.expiredCode();
        }

        User findUser = userRepo.findById(userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));

        String token = jwtHelper.generateToken(findUser);

        confirmationCodeRepo.deleteById(confirmationCode.getId());

        return new UserDto.AuthResponse(findUser.getId(), token, "Account created Successful!");
    }

    @org.springframework.transaction.annotation.Transactional
    public void resendConfirmationCode (UUID userId) {

        ConfirmationCode findConfirmationCode = confirmationCodeRepo.findByUserId(userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));

        String code = generateRandomCode.generateRandomCode();

        System.out.println("Old code we got: " + findConfirmationCode.getCode());
        System.out.println("Code we resend: " + code);

        findConfirmationCode.resendCode(code);

        confirmationCodeRepo.save(findConfirmationCode);
    }

    public void saveExpoPushToken (UUID userId, String token) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(() -> CustomResponseException.idIsNotFound(userId));

        findUser.setExpoPushToken(token);

        userRepo.save(findUser);
    }
}
