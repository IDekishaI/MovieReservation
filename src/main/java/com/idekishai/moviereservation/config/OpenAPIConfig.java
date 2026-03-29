package com.idekishai.moviereservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI cookingMenuOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Reservation API")
                        .description("REST API for booking movie seats for different movies and show times at theatres")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dejan Tasic")
                                .email("dejantasic2005@gmail.com")
                                .url("https://github.com/IDekishaI"))
                );
    }
}
