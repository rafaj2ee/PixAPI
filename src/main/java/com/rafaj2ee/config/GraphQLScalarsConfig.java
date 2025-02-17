package com.rafaj2ee.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLScalarsConfig {

    @Bean
    public GraphQLScalarType longScalar() {
        return ExtendedScalars.GraphQLLong;
    }

    @Bean
    public GraphQLScalarType bigDecimalScalar() {
        return ExtendedScalars.GraphQLBigDecimal;
    }
}