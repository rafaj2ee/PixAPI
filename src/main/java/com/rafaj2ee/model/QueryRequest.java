package com.rafaj2ee.model;

import lombok.Data;
import java.util.List;

@Data
public class QueryRequest {
    private List<String> queries;
}
