package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.NotaRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotaRepository notaRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        return "tickets/index";
    }

    @GetMapping("/{id}")
    public String show(Model model, @PathVariable Integer id) {
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("note", notaRepository.findAll());
        return "tickets/show";
    }

    @GetMapping("/create")
    public String create(Model model, Authentication authentication) {
        Ticket ticket = new Ticket();
        List<User> utentiAttivi = new ArrayList<>();

        for (User singelUser : userRepository.findAll()) {
            if (singelUser.getStatoPersonale().equals("Attivo") && singelUser.getRoles().){ // iniziare da qui
                utentiAttivi.add(singelUser);
        }
    }
        ticket.setDataCreazione(LocalDateTime.now());
        model.addAttribute("ticket", ticket);
        model.addAttribute("users", utentiAttivi);
        return "tickets/create";
    
    
}

    @PostMapping
    public String store(@Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "tickets/create";
        }
        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable Integer id) {
        List<User> utentiAttivi = new ArrayList<>();

        for (User singelUser : userRepository.findAll()) {
            if (singelUser.getStatoPersonale().equals("Attivo"))
                utentiAttivi.add(singelUser);
        }
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("users", utentiAttivi);
        return "tickets/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "tickets/edit";
        }

        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/editStato")
    public String editStato(Model model, @PathVariable Integer id) {
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("users", userRepository.findAll());
        return "tickets/editStato";
    }

    @PostMapping("/{id}/editStato")
    public String updateStato(@PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "tickets/edit";
        }

        ticketRepository.save(formTicket);
        return "redirect:/tickets/{id}";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/nota")
    public String nota(@PathVariable Integer id, Model model) {
        Nota nota = new Nota();
        nota.setTicket(ticketRepository.findById(id).get());
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("nota", nota);
        return "note/create";
    }

}
