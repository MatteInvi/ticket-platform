package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import java.util.List;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    public List<Ticket> findByTitoloContainingIgnoreCase(String titolo);
    public List<Ticket> findByStato(String stato);
    public List<Ticket> findByUser(User user);
    public List<Ticket> findByUserAndStato(User user,String stato);
    
}
