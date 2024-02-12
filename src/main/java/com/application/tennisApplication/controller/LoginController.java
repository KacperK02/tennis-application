package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.UserRepository;
import com.application.tennisApplication.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model){
        User user = userRepository.findByEmail(email);

        if(user != null && passwordEncoder.matches(password, user.getPassword()) && session.getAttribute("user") == null){
            userRepository.save(user);
            session.setAttribute("user", user);
            return "account";
        }
        else if (user != null && passwordEncoder.matches(password, user.getPassword()) && session.getAttribute("user") != null){
            session.setAttribute("user", user);
            model.addAttribute("login_error", "Jesteś już zalogowany na swoje konto.");
            return "index";
        }
        else{
            model.addAttribute("login_error", "Nieprawidłowy email lub hasło. Spróbuj ponownie.");
            return "index";
        }
    }
}
