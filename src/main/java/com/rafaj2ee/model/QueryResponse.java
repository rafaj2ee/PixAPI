package com.rafaj2ee.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class QueryResponse {
    private List<Map<String, Object>> results;
}
