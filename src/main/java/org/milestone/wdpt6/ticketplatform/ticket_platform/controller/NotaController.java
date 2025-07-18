package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.NotaRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/note")
public class NotaController {

    @Autowired
    NotaRepository notaRepository;

    @Autowired
    TicketRepository ticketRepository;



    @PostMapping
    public String store(@ModelAttribute("nota") Nota formNota, Model model){
        notaRepository.save(formNota);
        return "redirect:/tickets";
    }


    
}
