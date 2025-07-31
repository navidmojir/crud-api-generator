package com.example.demo.repositories;

import com.example.demo.entities.Xxx;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface XxxRepo extends CrudRepository<Xxx, Long>,
        JpaSpecificationExecutor<Xxx> {
}