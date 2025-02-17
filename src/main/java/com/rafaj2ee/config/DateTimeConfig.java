package com.rafaj2ee.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import graphql.language.StringValue;
import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

@Configuration
public class DateTimeConfig {
    
    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return ExtendedScalars.DateTime;
    }
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Bean
        public GraphQLScalarType localDateTimeScalar() {
            return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Java 8 LocalDateTime scalar")
                .coercing(new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(FORMATTER);
                        }
                        throw new IllegalArgumentException("Invalid LocalDateTime: " + dataFetcherResult);
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) {
                        return LocalDateTime.parse(input.toString(), FORMATTER);
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) {
                        if (input instanceof StringValue) {
                            return LocalDateTime.parse(((StringValue) input).getValue(), FORMATTER);
                        }
                        return null;
                    }
                })
                .build();
        }
}