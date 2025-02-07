package com.rafaj2ee.service;

import com.rafaj2ee.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    public List<Object> executeQueries(List<String> queries) {
        List<Object> results = new ArrayList<>();
        for (String query : queries) {
            Object result = queryRepository.executeQuery(query);
            results.add(result);
        }
        return results;
    }
}
