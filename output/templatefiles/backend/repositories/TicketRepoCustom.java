package com.example.demo.repositories;

import com.example.demo.entities.Ticket;
import com.example.demo.rest_controllers.TicketSearchFilter;
import ir.mojir.spring_boot_commons.dtos.SearchDto;
import ir.mojir.spring_boot_commons.helpers.RepositoryHelper;
import ir.mojir.spring_boot_commons.helpers.Validations;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketRepoCustom {
    @Autowired
    private TicketRepo ticketRepo;

    public Page<Ticket> search(SearchDto<TicketSearchFilter> req)
    {
        Pageable pageable = RepositoryHelper.generatePageRequestWithSort(
                req.getPaging(), req.getSorting());

        return ticketRepo.findAll(new Specification<Ticket>() {

            @Override
            public Predicate toPredicate(Root<Ticket> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = makePredicates(req, criteriaBuilder, root);

                return criteriaBuilder.and(
                        predicates.toArray(new Predicate[predicates.size()]));
            }


        }, pageable);
    }

    private List<Predicate> makePredicates(SearchDto<TicketSearchFilter> req, CriteriaBuilder cb,
                                           Root<Ticket> root) {
        List<Predicate> predicates = new ArrayList<>();
        TicketSearchFilter filters = req.getFilters();
        if(filters != null)
        {
            if(!Validations.isBlank(filters.getName()))
                predicates.add(cb.like(root.get("name"), "%" + filters.getName().trim() + "%"));
        }
        return predicates;
    }


}