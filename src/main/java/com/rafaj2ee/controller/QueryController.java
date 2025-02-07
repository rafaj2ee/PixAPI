package com.rafaj2ee.controller;

import com.rafaj2ee.service.QueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/queries")
public class QueryController {
    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public Object executeQueries(@RequestParam List<String> queries) {
        return queryService.executeQueries(queries);
    }
}
