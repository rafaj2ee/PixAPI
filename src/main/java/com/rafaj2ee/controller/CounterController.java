package com.rafaj2ee.controller;

import com.rafaj2ee.model.Counter;
import com.rafaj2ee.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counters")
public class CounterController {

    @Autowired
    private CounterService counterService;

    @GetMapping
    public List<Counter> getAllCounters() {
        return counterService.findAllCounters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Counter> getCounterById(@PathVariable Long id) {
        Counter counter = counterService.findCounterById(id);
        if (counter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(counter);
    }

    @GetMapping("/search")
    public List<Counter> getCountersByName(@RequestParam String name) {
        return counterService.findCountersByName(name);
    }

    @PostMapping
    public Counter createCounter(@RequestBody Counter counter) {
        return counterService.saveCounter(counter);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Counter> updateCounter(@PathVariable Long id, @RequestBody Counter counterDetails) {
//        Counter counter = counterService.findCounterById(id);
//        if (counter == null) {
//            return ResponseEntity.notFound().build();
//        }
//        counter.setCount(counterDetails.getCount());
//        counter.setName(counterDetails.getName());
//        final Counter updatedCounter = counterService.saveCounter(counter);
//        return ResponseEntity.ok(updatedCounter);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCounter(@PathVariable Long id) {
//        Counter counter = counterService.findCounterById(id);
//        if (counter == null) {
//            return ResponseEntity.notFound().build();
//        }
//        counterService.deleteCounter(id);
//        return ResponseEntity.noContent().build();
//    }
}
