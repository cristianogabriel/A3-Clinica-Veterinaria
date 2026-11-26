package com.clinica.ibmr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI clinicaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API privada da Clínica Veterinária")
                .version("1.0.0")
                .description("API interna da clínica. A integração governamental externa não está implementada."));
    }
}
