package com.example.demo.services;

import com.example.demo.dtos.xxx.CreateXxxReq;
import com.example.demo.dtos.xxx.UpdateXxxReq;
import com.example.demo.entities.Xxx;
import com.example.demo.repositories.XxxRepo;
import com.example.demo.repositories.XxxRepoCustom;
import com.example.demo.dtos.xxx.XxxSearchFilter;
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
public class XxxService {

    private static Logger logger = LoggerFactory.getLogger(XxxService.class);

    @Autowired
    private XxxRepo xxxRepo;

    @Autowired
    private XxxRepoCustom xxxRepoCustom;


    @Transactional
    public Xxx create(CreateXxxReq req)
    {
        Xxx entity = new Xxx();
        entity.setName(req.getName());

        Xxx savedEntity = xxxRepo.save(entity);

        logger.info("A xxx with id {} was created.", savedEntity.getId());

        return savedEntity;
    }

    public Xxx get(long id) {
        Xxx xxx = findById(id);
        logger.info("Xxx with id {} was retrieved", id);
        return xxx;
    }

    public Xxx findById(long id) {
        Optional<Xxx> optXxx = xxxRepo.findById(id);
        if(optXxx.isEmpty())
            throw new EntityNotFoundException(id, null);
        return optXxx.get();
    }

    public Xxx update(long id, UpdateXxxReq req) {
        Xxx xxx = findById(id);
        xxx.setName(req.getName());
        Xxx savedEntity = xxxRepo.save(xxx);
        logger.info("Xxx with id {} was updated", id);
        return savedEntity;
    }

    public void delete(long id) {
        Xxx xxx = findById(id);
        xxxRepo.delete(xxx);
        logger.info("Xxx with id {} was deleted", id);
    }

    public Page<Xxx> search(SearchDto<XxxSearchFilter> req) {
        logger.info(("Searching for xxx"));
        return xxxRepoCustom.search(req);
    }
}
