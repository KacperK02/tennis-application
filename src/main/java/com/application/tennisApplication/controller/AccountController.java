package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.UserRepository;
import com.application.tennisApplication.service.FollowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class AccountController {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/account")
    public ResponseEntity<List<Player>> account(HttpSession session){
        if (session.getAttribute("user") != null){
            User user = (User) session.getAttribute("user");
            List<Player> followedPlayers = followService.getFollowedPlayers(user.getUserID());
            return ResponseEntity.ok(followedPlayers);
        }
        else{
            return null;
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            return ResponseEntity.ok(userInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/change-username")
    public ResponseEntity<Map<String, String>> changeUsername(HttpSession session, @RequestBody Map<String, String> requestBody) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Użytkownik nie jest zalogowany."));
        }

        String newUsername = requestBody.get("newUsername");
        String password = requestBody.get("password");

        // Sprawdzanie poprawności hasła
        if (!passwordEncoder.matches(password, currentUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Niepoprawne hasło."));
        }

        // Sprawdzanie, czy nazwa użytkownika już istnieje w bazie danych
        if (userRepository.findByUsername(newUsername) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Użytkownik o tej nazwie już istnieje."));
        }

        // Aktualizacja nazwy użytkownika
        currentUser.setUsername(newUsername);
        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "Nazwa użytkownika została zmieniona."));
    }
}
