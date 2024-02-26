package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.UserRepository;
import com.application.tennisApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public String registration(@ModelAttribute User user, Model model){
        if(userRepository.findByUsername(user.getUsername()) != null){
            model.addAttribute("error", "Podana nazwa użytkownika jest już zajęta");
            return "register";
        }

        if(userRepository.findByEmail(user.getEmail()) != null){
            model.addAttribute("error", "Podany adres email jest już zajęty");
            return "register";
        }

        if(!userService.isPasswordValid(user.getPassword())){
            model.addAttribute("error", "Hasło musi zawierać co najmniej 8 znaków, jedną małą i jedną dużą literę oraz znak specjalny");
            return "register";
        }

        if(!userService.isEmailValid(user.getEmail())){
            model.addAttribute("error", "Podany adres email jest niepoprawny");
            return "register";
        }

        userService.saveUser(user);
        model.addAttribute("registration_completed", "Konto zostało utworzone pomyślnie! Możesz się teraz zalogować.");
        return "index";
    }
}
