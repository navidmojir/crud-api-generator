package ir.mojir.simple_ticketing_system.services;

import ir.mojir.simple_ticketing_system.dtos.ticket.CreateTicketReq;
import ir.mojir.simple_ticketing_system.dtos.ticket.UpdateTicketReq;
import ir.mojir.simple_ticketing_system.entities.Ticket;
import ir.mojir.simple_ticketing_system.repositories.TicketRepo;
import ir.mojir.simple_ticketing_system.repositories.TicketRepoCustom;
import ir.mojir.simple_ticketing_system.rest_controllers.TicketSearchFilter;
import ir.mojir.spring_boot_commons.dtos.SearchDto;
import ir.mojir.spring_boot_commons.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketService {

    private static Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private TicketRepoCustom ticketRepoCustom;


    @Transactional
    public Ticket create(CreateTicketReq req)
    {
        Ticket entity = new Ticket();
        entity.setName(req.getName());

        Ticket savedEntity = ticketRepo.save(entity);

        logger.info("A ticket with id {} was created.", savedEntity.getId());

        return savedEntity;
    }

    public Ticket get(long id) {
        Ticket ticket = findById(id);
        logger.info("Ticket with id {} was retrieved", id);
        return ticket;
    }

    public Ticket findById(long id) {
        Optional<Ticket> optTicket = ticketRepo.findById(id);
        if(optTicket.isEmpty())
            throw new EntityNotFoundException(id, null);
        return optTicket.get();
    }

    public Ticket update(long id, UpdateTicketReq req) {
        Ticket ticket = findById(id);
        ticket.setName(req.getName());
        Ticket savedEntity = ticketRepo.save(ticket);
        logger.info("Ticket with id {} was updated", id);
        return savedEntity;
    }

    public void delete(long id) {
        Ticket ticket = findById(id);
        ticketRepo.delete(ticket);
        logger.info("Ticket with id {} was deleted", id);
    }

    public Page<Ticket> search(SearchDto<TicketSearchFilter> req) {
        logger.info(("Searching for ticket"));
        return ticketRepoCustom.search(req);
    }
}
