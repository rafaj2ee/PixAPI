package com.rafaj2ee.service;

import com.rafaj2ee.model.Counter;
import com.rafaj2ee.repository.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounterService {

    @Autowired
    private CounterRepository counterRepository;

    public List<Counter> findAllCounters() {
        return counterRepository.findAll();
    }

    public Counter findCounterById(Long id) {
        return counterRepository.findById(id).orElse(null);
    }

    public List<Counter> findCountersByName(String name) {
        return counterRepository.findByName(name);
    }

    public Counter findOrCreateCounterByName(String name) {
        List<Counter> counters = counterRepository.findByName(name);
        Counter counter;
        if (counters.isEmpty()) {
            counter = new Counter();
            counter.setName(name);
            counter.setCount(1);
        } else {
            counter = counters.get(0);
            counter.setCount(counter.getCount() + 1);
        }
        return counterRepository.save(counter);
    }

    public Counter saveCounter(Counter counter) {
        return counterRepository.save(counter);
    }

    public void deleteCounter(Long id) {
        counterRepository.deleteById(id);
    }
}
