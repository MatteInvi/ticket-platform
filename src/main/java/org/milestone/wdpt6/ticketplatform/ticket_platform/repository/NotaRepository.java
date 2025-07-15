package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepository extends JpaRepository<Nota, Integer> {
    
}
