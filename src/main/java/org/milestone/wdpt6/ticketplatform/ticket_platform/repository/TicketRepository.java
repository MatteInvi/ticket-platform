package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import java.util.List;
import java.util.Optional;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Optional<User> findByUserId(Integer id);
    public List<Ticket> findByTitoloContainingIgnoreCase(String titolo);
    
}
