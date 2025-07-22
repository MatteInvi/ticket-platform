package org.milestone.wdpt6.ticketplatform.ticket_platform.repository;

import org.milestone.wdpt6.ticketplatform.ticket_platform.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
    
}
