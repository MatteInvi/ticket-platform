package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.util.Optional;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
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
@RequestMapping("/note")
public class NotaController {

    @Autowired
    NotaRepository notaRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @PostMapping
    public String store(@Valid @ModelAttribute("nota") Nota formNota, BindingResult bindingResult, Model model) {
        Integer idTicket = formNota.getTicket().getId();
        if (bindingResult.hasErrors()) {
            model.addAttribute("nota", formNota);
            return "note/create";
        }

        notaRepository.save(formNota);
        return "redirect:/tickets/" + idTicket;
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
        int idTicket = notaRepository.findById(id).get().getTicket().getId();
        // Verifico il tipo di autorizzazzione...
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            // Se è un admin modifico una nota a prescindere
            if (authority.getAuthority().equals("ADMIN")) {
                model.addAttribute("nota", notaRepository.findById(id).get());
                return "note/edit";
                // Se è un operatore modifico una nota solo se è di un suo ticket
            } else if (ticketRepository.findById(idTicket).get().getUser() == utenteLoggato.get()) {
                model.addAttribute("nota", notaRepository.findById(id).get());
                return "note/edit";
            }
        }

        return "HttpStatus.NOT_FOUND";

    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("nota") Nota formNota,
            BindingResult bindingResult, Model model) {
        Integer idTicket = formNota.getTicket().getId();
        if (bindingResult.hasErrors()) {
            model.addAttribute("nota", formNota);
            return "nota/edit";
        }

        notaRepository.save(formNota);
        return "redirect:/tickets/" + idTicket;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
        int idTicket = notaRepository.findById(id).get().getTicket().getId();
        // Verifico il tipo di autorizzazzione...
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            // Se è un admin elimino una nota a prescindere
            if (authority.getAuthority().equals("ADMIN")) {
                notaRepository.deleteById(id);
                return "redirect:/tickets/" + idTicket;
                // Se è un operatore elimino una nota solo se è di un suo ticket
            } else if (ticketRepository.findById(idTicket).get().getUser() == utenteLoggato.get()
                    && authority.getAuthority().equals("OPERATORE")) {
                notaRepository.deleteById(id);
                return "redirect:/tickets/" + idTicket;
            }
        }

        return "HttpStatus.NOT_FOUND";

    }

}
