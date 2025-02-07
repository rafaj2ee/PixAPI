package com.rafaj2ee.repository;

import com.rafaj2ee.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommandRepository extends JpaRepository<Command, Long>, JpaSpecificationExecutor<Command> {
}
