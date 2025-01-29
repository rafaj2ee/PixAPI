package com.rafaj2ee.repository;

import com.rafaj2ee.model.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {
    List<Counter> findByName(String name);
}
