package com.nelliebly.routeserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info().title("Route Server API")
				.version("1.0")
				.description("API for route calculation and POI services"))
			.servers(List.of(new Server().url("http://localhost:8080").description("Local server")));
	}

}
