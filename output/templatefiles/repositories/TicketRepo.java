package ir.mojir.simple_ticketing_system.repositories;

import ir.mojir.simple_ticketing_system.entities.Ticket;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepo extends CrudRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket> {
}