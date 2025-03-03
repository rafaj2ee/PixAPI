package com.rafaj2ee.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "PixAPI",
        version = "1.0",
        description = "API para geração de QR Codes Pix"
    )
)
public class SwaggerConfig {
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("all-endpoints")
            .pathsToMatch("/**") // Captura TODOS os endpoints
            .build();
    }
}