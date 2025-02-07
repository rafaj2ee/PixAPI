// src/main/java/com/rafaj2ee/controller/CommandController.java
package com.rafaj2ee.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rafaj2ee.model.Command;
import com.rafaj2ee.service.CommandService;

@RestController
@RequestMapping("/api/v1/command")
public class CommandController {
    @Value("${mypass}")
    private String pass;
    @Autowired
    private CommandService commandService;

    @PostMapping("/execute")
    public ResponseEntity<String> executeCommand(@RequestParam(required = false) String myPass,
    		@RequestBody Command command) {
        try {
        	if(pass.equals(myPass)) {
        		String result = commandService.executeCommand(command.getCommandLine());
        		return ResponseEntity.ok(result);
        	} else {
        		throw new Exception("Wrong Password");
        	}
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error executing command: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Command>> getCommands(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String command,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate executedAt) {
        List<Command> commands = commandService.findCommands(id, command, executedAt);
        return ResponseEntity.ok(commands);
    }
}
