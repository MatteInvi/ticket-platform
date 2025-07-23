package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import java.util.List;
import java.util.Optional;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {

     Optional<User> findByEmail(String email);
     public List<User> findByNomeContainingIgnoreCase(String nome);
    
}
