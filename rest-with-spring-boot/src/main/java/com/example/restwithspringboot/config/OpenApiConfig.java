package com.example.restwithspringboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
            .info(new Info()
                .title("RESTful API with java and Spring Boot 3")
                .version("v1")
                .description("This is a person API")
                .termsOfService("https://pub.erudio.com.br/meus-cursos")
                .license(
                    new License().name("Apache 2.0")
                        .url("https://pub.erudio.com.br/meus-cursos")));
    }
}
