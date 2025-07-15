package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ticket")
public class TicketController {
    

    @Autowired
    TicketRepository ticketRepository;

    @GetMapping
    public String index(Model model){
        model.addAttribute("tickets", ticketRepository.findAll());
        return "ticket/index";
    }

}
