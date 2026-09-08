package com.chat_app.chat_app.Core_System.User;

import com.chat_app.chat_app.Shared.ErrorHandling.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get-all-users")
    public ResponseEntity<GlobalResponse<List<User>>> getAllUsers () {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(new GlobalResponse<>(users), HttpStatus.OK);
    }

    @GetMapping("/get-user-by-id/{userId}")
    public ResponseEntity<GlobalResponse<User>> getById(@PathVariable UUID userId ){
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(new GlobalResponse<>(user), HttpStatus.OK);
    }

    @PostMapping("/sigh-up")
    public ResponseEntity<GlobalResponse<UserDto.AuthResponse>> SighUp (@RequestBody @Valid UserDto.CreateUser createUser){
        UserDto.AuthResponse response = userService.createUser(createUser);
        return new ResponseEntity<>(new GlobalResponse<>(response), HttpStatus.OK);
    }

    @PostMapping("/sigh-in")
    public ResponseEntity<GlobalResponse<UserDto.AuthResponse>> sighIn (@RequestBody @Valid UserDto.SighIn sighIn){
        UserDto.AuthResponse response = userService.sighIn(sighIn);
        return new ResponseEntity<>(new GlobalResponse<>(response), HttpStatus.OK);
    }


    @PutMapping("/update-user/{userId}")
    public ResponseEntity<GlobalResponse<String>> updateUser(@PathVariable @Valid UUID userId, @RequestBody @Valid UserDto.UpdateUser updateUser){
        userService.updateUser(userId, updateUser);
        return new ResponseEntity<>(new GlobalResponse<>("User updated successfully!"), HttpStatus.OK);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<GlobalResponse<String>> deleteUserById(@PathVariable UUID userId){
        userService.deleteUser(userId);
        return new ResponseEntity<>(new GlobalResponse<>("User deleted successfully!"), HttpStatus.OK);
    }
}
