package com.example.demo.repositories;

import com.example.demo.entities.Ticket;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepo extends CrudRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket> {
}