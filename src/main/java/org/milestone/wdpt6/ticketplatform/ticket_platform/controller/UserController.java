package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.NotaRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.RoleRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    NotaRepository notaRepository;

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {
        List<User> utenti = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            utenti = userRepository.findByNomeContainingIgnoreCase(keyword);
        } else {
            utenti = userRepository.findUtentiOperatori();
        }
        model.addAttribute("utenti", utenti);
        return "users/index";
    }

    @GetMapping("/show")
    public String show(Model model, Authentication authentication) {
        model.addAttribute("utente", userRepository.findByEmail(authentication.getName()).get());
        return "users/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        User nuovoUtente = new User();
        model.addAttribute("user", nuovoUtente);
        return "users/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("user") User userForm, BindingResult bindingResult,
            Authentication authentication, Model model) {

        if (bindingResult.hasErrors()) {
            return "users/create";
        }
        if (userRepository.existsByEmail(userForm.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email già registrata da un altro utente");
            return "users/create";
        }

        // Salvo l'id del ruolo nel setRoles
        // Inserisco il password encoder e salvo
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        userRepository.save(userForm);
        return "redirect:/user";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable Integer id) {
        // Passo in modifica solo nome e email per non esporre la password cryptata
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@Valid @ModelAttribute("user") User userForm, BindingResult bindingResult,Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        if (userRepository.existsByEmailAndIdNot(userForm.getEmail(), userForm.getId())) {
            bindingResult.rejectValue("email", "error.user", "Email già registrata da un altro utente");
            return "users/edit";
        }
        
        // Salvo l'id del ruolo nel setRoles
        // Inserisco il password encoder e salvo
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        userRepository.save(userForm);
        return "redirect:/user";
    }

    @GetMapping("/editStato")
    public String editStato(Model model, Authentication authentication) {
        model.addAttribute("utente", userRepository.findByEmail(authentication.getName()).get());
        return "users/editStato";
    }

    @PostMapping("/editStato")
    public String updateStato(@Valid @ModelAttribute("utente") User userForm, BindingResult bindingResult,
            Authentication authentication, Model model) {
        Optional<User> userLoggato = userRepository.findByEmail(authentication.getName());
        boolean isCompleted = true;

        if (userLoggato.get().getStatoPersonale().equals("Disponibile")) {
            // Se lo stato personale è attivo esegui il controllo
            // per vedere se deve completare dei ticket altrimenti non può modificarlo
            for (Ticket singleTicket : userLoggato.get().getTickets()) {
                if (singleTicket.getUser().equals(userLoggato.get()) && !singleTicket.getStato().equals("Completato")) {
                    isCompleted = false;
                    break;
                }
            }
            if (isCompleted) {
                // Cambia lo stato se non ha ticket aperti (setto il ruolo e la password che non
                // viene caricato dal form)
                userForm.setRoles(userLoggato.get().getRoles());
                userForm.setPassword(userLoggato.get().getPassword());
                userRepository.save(userForm);
                return "redirect:/user/show";
            }
            // Non cambia lo stato se deve completare dei ticket
            bindingResult.rejectValue("statoPersonale", "error.utente",
                    "Non puoi cambiare il tuo stato se hai ticket aperti!");
            return "users/editStato";
        }

        // Se non è attivo invece può modificare lo stato a prescindere (setto il ruolo
        // e la password che non viene caricato dal form)
        userForm.setRoles(userLoggato.get().getRoles());
        userForm.setPassword(userLoggato.get().getPassword());

        userRepository.save(userForm);
        return "redirect:/user/show";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        // Creo una lista di note per ogni ticket di questo utente e le elimino
        // Successivamente elimino il ticket
        List<Ticket> userTickets = ticketRepository.findByUser(user.get());
        for (Ticket singleTicket : userTickets) {
            List<Nota> ticketNotes = notaRepository.findByTicket(singleTicket);
            notaRepository.deleteAll(ticketNotes);
        }
        ticketRepository.deleteAll(userTickets);

        userRepository.deleteById(id);
        return "redirect:/user";
    }

}
