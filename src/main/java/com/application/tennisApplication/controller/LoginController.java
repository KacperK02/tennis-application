package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.UserRepository;
import com.application.tennisApplication.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@CrossOrigin
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData, HttpSession session) {

        if (session.getAttribute("user") != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Jesteś już zalogowany na swoje konto."));
        }

        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(Map.of("status", "success", "redirectUrl", "/account"));
        }
        else {
            return ResponseEntity.badRequest().body(Map.of("error", "Nieprawidłowy email lub hasło. Spróbuj ponownie."));
        }
    }

    @GetMapping("/checkSession")
    public ResponseEntity<Map<String, Boolean>> checkSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(Map.of("loggedIn", true));
        } else {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Unieważnienie sesji
        return ResponseEntity.ok().build();
    }

}
