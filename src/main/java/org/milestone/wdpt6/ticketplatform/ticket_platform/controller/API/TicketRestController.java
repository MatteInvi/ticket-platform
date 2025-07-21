package org.milestone.wdpt6.ticketplatform.ticket_platform.controller.API;

import java.util.ArrayList;
import java.util.List;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tickets")
public class TicketRestController {

    @Autowired
    TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<List<Ticket>> index() {

        List<Ticket> tickets = ticketRepository.findAll();

        if (tickets.size() == 0) {
            return new ResponseEntity<>(tickets, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);

    }

    @GetMapping("/{stato}")
    public ResponseEntity<List<Ticket>> filtroStato(@PathVariable String stato) {
        List<Ticket> ticketFiltrati = new ArrayList<>();
        for (Ticket singleTicket : ticketRepository.findAll()) {
            if (singleTicket.getStato().equals(stato)) {
                ticketFiltrati.add(singleTicket);
            }

        }
        if (ticketFiltrati.size() == 0) {
            return new ResponseEntity<>(ticketFiltrati, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(ticketFiltrati, HttpStatus.OK);
    }

}
