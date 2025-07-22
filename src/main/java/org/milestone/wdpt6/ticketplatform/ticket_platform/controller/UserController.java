package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Role;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @GetMapping
    public String index(Model model) {
        List<User> operatori = new ArrayList<>();
        for (User singleUser : userRepository.findAll()) {
            for (Role singleRole : singleUser.getRoles()) {
                if (singleRole.getNome().equals("OPERATORE")) {
                    operatori.add(singleUser);
                } 
            }
        }
        model.addAttribute("utenti", operatori);
        return "users/index";
    }

    @GetMapping("/show")
    public String show(Model model, Authentication authentication) {
        model.addAttribute("utente", userRepository.findByEmail(authentication.getName()).get());
        return "users/show";
    }

    @GetMapping("/editStato")
    public String stato(Model model, Authentication authentication) {
        model.addAttribute("utente", userRepository.findByEmail(authentication.getName()).get());

        return "users/editStato";
    }

    @PostMapping("/editStato")
    public String updateStato(@ModelAttribute("utente") User userForm, Authentication authentication, Model model) {
        Optional<User> userLoggato = userRepository.findByEmail(authentication.getName());
        int nTicketNonCompleti = 0;
        if (userLoggato.get().getStatoPersonale().equals("Disponibile")) {
            // Se lo stato personale è attivo esegui il controllo
            // per vedere se deve completare dei ticket altrimenti non può modificarlo
            for (Ticket singleTicket : ticketRepository.findAll()) {
                if (singleTicket.getUser().equals(userLoggato.get()) && !singleTicket.getStato().equals("Completato")) {
                    nTicketNonCompleti = nTicketNonCompleti + 1;

                }

            }
            if (nTicketNonCompleti == 0) {
                // Cambia lo stato se non ha ticket aperti (setto il ruolo e la password che non
                // viene caricato dal form)
                userForm.setRoles(userLoggato.get().getRoles());
                userForm.setPassword(userLoggato.get().getPassword());
                userRepository.save(userForm);
                return "redirect:/user/editStato";
            }
            // Non cambia lo stato se deve completare dei ticket
            model.addAttribute("err", "Non puoi cambiare il tuo stato se hai ticket aperti!");
            return "users/editStato";
        }
        // Se non è attivo invece può modificare lo stato a prescindere (setto il ruolo
        // e la password che non viene caricato dal form)
        userForm.setRoles(userLoggato.get().getRoles());
        userForm.setPassword(userLoggato.get().getPassword());
        userRepository.save(userForm);
        return "redirect:/user/editStato";
    }

}
