package org.milestone.wdpt6.ticketplatform.ticket_platform.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.CategoryRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.NotaRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping
    public String index(Model model, Authentication authentication, @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String filtro) {
        Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
        List<Ticket> tickets = new ArrayList<>();

        // Controllo authority per mostrare i ticket giusti
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN")) {
                if (keyword != null && !keyword.isEmpty()) {
                    tickets = ticketRepository.findByTitoloContainingIgnoreCase(keyword);
                } else {
                    tickets = ticketRepository.findAll(Sort.by(Sort.Direction.ASC, "stato"));
                }
                if (filtro != null && !filtro.isEmpty()) {
                    tickets = ticketRepository.findByStato(filtro);
                }

            } else if (authority.getAuthority().equals("OPERATORE")) {
                List<Ticket> ticketsUser = ticketRepository.findByUser(utenteLoggato.get());
                for (Ticket singleTicket : ticketsUser) {
                    tickets.add(singleTicket);
                }
                if (filtro != null && !filtro.isEmpty()) {
                    tickets = ticketRepository.findByUserAndStato(utenteLoggato.get(), filtro);
                }

            }
            model.addAttribute("tickets", tickets);
        }

        return "tickets/index";
    }

    @GetMapping("/{id}")
    public String show(Model model, @PathVariable Integer id, Authentication authentication) {
        Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
        // Verifico il tipo di autorizzazzione...
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            // Se è un admin mostro tutti i ticket
            if (authority.getAuthority().equals("ADMIN")) {
                model.addAttribute("ticket", ticketRepository.findById(id).get());
                model.addAttribute("note", notaRepository.findAll());
                return "tickets/show";
                // Se è un operatore mostro solo i ticket a lui assegnati
            } else if (ticketRepository.findById(id).get().getUser() == utenteLoggato.get()) {
                model.addAttribute("ticket", ticketRepository.findById(id).get());
                model.addAttribute("note", notaRepository.findAll());
                return "tickets/show";
            }
        }

        return "HttpStatus.NOT_FOUND";

    }

    @GetMapping("/create")
    public String create(Model model) {
        Ticket ticket = new Ticket();
        List<User> utentiAttivi = userRepository.findUtentiDisponibiliOperatore();

        ticket.setDataCreazione(LocalDateTime.now());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("ticket", ticket);
        model.addAttribute("users", utentiAttivi);
        return "tickets/create";

    }

    @PostMapping
    public String store(@Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            return "tickets/create";
        }
        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable Integer id) {
        List<User> utentiAttivi = userRepository.findUtentiDisponibiliOperatore();
        Optional<Ticket> tickeOptional = ticketRepository.findById(id);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("ticket", tickeOptional.get());
        model.addAttribute("users", utentiAttivi);
        return "tickets/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            return "tickets/edit";
        }

        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/editStato")
    public String editStato(Model model, @PathVariable Integer id, Authentication authentication) {
        Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
        Optional<Ticket> tickeOptional = ticketRepository.findById(id);

        if (tickeOptional.get().getUser() == utenteLoggato.get()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("ticket", tickeOptional.get());
            model.addAttribute("users", userRepository.findAll());
            return "tickets/editStato";
        }

        return "HttpStatus.NOT_FOUND";

    }

    @PostMapping("/{id}/editStato")
    public String updateStato(@PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            return "tickets/edit";
        }

        ticketRepository.save(formTicket);
        return "redirect:/tickets/{id}";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        for (Nota singleNota : ticketRepository.findById(id).get().getNote()) {
            notaRepository.delete(singleNota);
        }

        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }

    // NOTE
    @GetMapping("/{id}/nota")
    public String nota(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
        Nota nota = new Nota();
        // Verifico il tipo di autorizzazzione...
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            // Se è un admin creo la nota per qualsiasi ticket
            if (authority.getAuthority().equals("ADMIN")) {
                nota.setDataCreazione(LocalDateTime.now());
                nota.setTicket(ticketRepository.findById(id).get());
                nota.setUser(utenteLoggato.get());
                model.addAttribute("ticket", ticketRepository.findById(id).get());
                model.addAttribute("nota", nota);
                return "note/create";
            // Se è un operatore creo la nota solo se il ticket è assegnato a lui
            } else if (ticketRepository.findById(id).get().getUser() == utenteLoggato.get()) {
                nota.setDataCreazione(LocalDateTime.now());
                nota.setTicket(ticketRepository.findById(id).get());
                nota.setUser(utenteLoggato.get());
                model.addAttribute("ticket", ticketRepository.findById(id).get());
                model.addAttribute("nota", nota);
                return "note/create";
            }
        }


        return "HttpStatus.NOT_FOUND";

    }

}
