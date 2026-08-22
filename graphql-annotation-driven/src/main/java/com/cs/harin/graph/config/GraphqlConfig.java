package com.cs.harin.graph.config;

import graphql.schema.GraphQLScalarType;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphqlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            // register the Long scalar implementation for schema 'Long' scalar
            GraphQLScalarType longScalar = ExtendedScalars.GraphQLLong;
            wiringBuilder.scalar(longScalar);
        };
    }
}
