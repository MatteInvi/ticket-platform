package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import java.util.List;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepository extends JpaRepository<Nota, Integer> {
    public List<Nota> findByTicket(Ticket ticket);
    
}
