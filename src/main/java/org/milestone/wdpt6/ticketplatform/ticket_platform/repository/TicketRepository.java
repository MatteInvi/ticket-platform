package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    
}
