package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String homepage() {

        return "pages/home";
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

}
