package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.util.Optional;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String homepage(Model model, Authentication authentication) {
        Optional<User> utenteLoggato =  userRepository.findByEmail(authentication.getName());
        model.addAttribute("user", utenteLoggato.get());

        return "pages/home";
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

}
