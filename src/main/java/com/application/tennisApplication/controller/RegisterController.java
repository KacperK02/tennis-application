package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.UserRepository;
import com.application.tennisApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData){
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");

        if(userRepository.findByUsername(username) != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Użytkownik z taką nazwą już istnieje."));
        }

        if(userRepository.findByEmail(email) != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Użytkownik z tym emailem już istnieje."));
        }

        if(!userService.isPasswordValid(password)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Hasło musi zawierać 8 znaków, co najmniej jedną małą i jedną dużą literę oraz znak specjalny"));
        }

        if(!userService.isEmailValid(email)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Podany adres email jest niepoprawny"));
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("status", "success"));
    }
}
