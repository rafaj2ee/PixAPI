// src/main/java/com/rafaj2ee/service/CommandService.java
package com.rafaj2ee.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.rafaj2ee.model.Command;
import com.rafaj2ee.repository.CommandRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommandService {
	@Autowired
	CommandRepository commandRepository;
	
    public String executeCommand(String command) throws Exception {
        StringBuilder output = new StringBuilder();
        Process process = null;
        try {
        	process = Runtime.getRuntime().exec(command);
        	process.waitFor();
        } catch(Exception e) {
        	command = "cmd /c " + command;
        	process = Runtime.getRuntime().exec(command);
        	process.waitFor();        	
        }
        log.info(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        Command commandObj = new Command();
        commandObj.setCommandLine(command);
        commandObj.setExecutedAt(LocalDateTime.now());
        commandObj.setResult(output.toString());
        commandRepository.save(commandObj);
        return commandObj.getResult();
    }
    public List<Command> findCommands(Long id, String commandText, LocalDate executedAt) {
        Specification<Command> spec = Specification.where(null);

        if (id != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id));
        }

        if (commandText != null && !commandText.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("commandLine"), "%" + commandText + "%"));
        }

        if (executedAt!=null) {
            LocalDateTime startOfDay = executedAt.atStartOfDay();
            LocalDateTime endOfDay = executedAt.atTime(LocalTime.MAX);
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("executedAt"), startOfDay, endOfDay));
        }
        return commandRepository.findAll(spec);
    }
}
