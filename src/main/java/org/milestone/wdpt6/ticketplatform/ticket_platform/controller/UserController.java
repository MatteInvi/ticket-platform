package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Role;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
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

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {
        List<User> operatori = new ArrayList<>();
        List<User> utenti = new ArrayList<>();
        // Prima salvo tutti gli utenti in una lista (filtrando se richiesto)
        if (keyword != null && !keyword.isEmpty()) {
            utenti = userRepository.findByNomeContainingIgnoreCase(keyword);
        } else {
            utenti = userRepository.findAll();
        }
        // Successivamente li filtro in modo che siano solo operatori
        for (User singleUser : utenti) {
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

    @GetMapping("/create")
    public String create(Model model) {
        User nuovoUtente = new User();

        model.addAttribute("user", nuovoUtente);
        return "users/create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("user") User userForm, BindingResult bindingResult, Model model) {
        // Cerco il ruolo con id 2 (Operatore)
        Role roleOperatore = roleRepository.findById(2).get();
        userForm.setStatoPersonale("Non disponibile");

        if (bindingResult.hasErrors()) {
            return "users/create";
        }
        if (userRepository.existsByEmail(userForm.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email già registrata");
            return "users/create";
        }
        // Salvo l'id del ruolo nel setRoles
        // Inserisco il password encoder e salvo
        userForm.setRoles((Set.of(roleOperatore)));
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        userRepository.save(userForm);
        return "redirect:/user";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable Integer id, User userDetails) {
        // Passo in modifica solo nome e email per non esporre la password cryptata
        User user = userRepository.findById(id).get();
        user.setEmail(user.getEmail());
        user.setNome(user.getNome());
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/{id}")
    public String update(@Valid @ModelAttribute("user") User userForm, BindingResult bindingResult, Model model) {
        // Cerco il ruolo con id 2 (Operatore)
        Role roleOperatore = roleRepository.findById(2).get();

        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        if (userRepository.existsByEmailAndIdNot(userForm.getEmail(), userForm.getId())) {
            bindingResult.rejectValue("email", "error.user", "Email già registrata da un altro utente");
            return "users/edit";
        }
        if (userForm.getPassword()!=null && !userForm.getPassword().isBlank()){
              userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }
        // Salvo l'id del ruolo nel setRoles
        // Inserisco il password encoder e salvo
        userForm.setStatoPersonale("Non disponibile");
        userForm.setRoles((Set.of(roleOperatore)));
      
        userRepository.save(userForm);
        return "redirect:/user";
    }

    @GetMapping("/editStato")
    public String editStato(Model model, Authentication authentication) {
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
