package ch.app.cbt.router;

import ch.app.cbt.handler.CbtHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CbtRouter {

	@Bean
	public RouterFunction<ServerResponse> todoRoute(CbtHandler handler) {
		return RouterFunctions.route()
				.GET("/api/location", handler::getWeatherByLocation)
				.GET("/api/country", handler::getWeatherByCountry)
			.build();
	}
}
