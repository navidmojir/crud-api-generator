package com.example.demo.rest_controllers;

import com.example.demo.dtos.ticket.*;
import com.example.demo.entities.Ticket;
import com.example.demo.services.TicketService;
import ir.mojir.spring_boot_commons.dtos.SearchDto;
import ir.mojir.spring_boot_commons.helpers.PersianCharNormalizer;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public CreateTicketResp create(@Valid @RequestBody CreateTicketReq req)
    {
        new PersianCharNormalizer().normalize(req);
        Ticket ticket = ticketService.create(req);
        return mapper.map(ticket, CreateTicketResp.class);
    }

    @GetMapping("/{id}")
    public GetTicketResp get(@PathVariable long id)
    {
        Ticket entity = ticketService.get(id);
        return mapper.map(entity, GetTicketResp.class);
    }

    @PutMapping("/{id}")
    public UpdateTicketResp update(@PathVariable long id, @Valid @RequestBody UpdateTicketReq req)
    {
        new PersianCharNormalizer().normalize(req);
        Ticket ticket = ticketService.update(id, req);
        return mapper.map(ticket, UpdateTicketResp.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        ticketService.delete(id);
    }


    @PostMapping("/search")
    public ResponseEntity<List<SearchTicketRespRow>> search(@Valid @RequestBody SearchDto<TicketSearchFilter> req)
    {
        new PersianCharNormalizer().normalize(req.getFilters());
        Page<Ticket> result = ticketService.search(req);
        return ResponseEntity.ok()
                .header("X-TOTAL-COUNT", String.valueOf(result.getTotalElements()))
                .body(result.getContent().stream().map((p)->mapper.map(p, SearchTicketRespRow.class))
                        .collect(Collectors.toList()));
    }

}
