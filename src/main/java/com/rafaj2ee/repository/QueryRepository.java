package com.rafaj2ee.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class QueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public QueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object executeQuery(String query) {
    	if(query.contains("SELECT")&&!query.contains("LIMIT")) {
    		query = query+ " LIMIT 1000";
    	}
    	if((query.contains("INSERT")||
        		query.contains("UPDATE")||
        		query.contains("DELETE"))
        		&&!query.equals("<string>")) {
        		jdbcTemplate.execute(query);
        		return query;
        } else if(query.contains("SELECT")&&!query.equals("<string>")) {
    		return jdbcTemplate.queryForList(query);
    	}

    	return "";
    }
}
 		