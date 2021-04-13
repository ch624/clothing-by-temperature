package ch.app.cbt.handler;

import ch.app.cbt.model.Country;
import ch.app.cbt.repository.CbtRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class CbtHandler {
	private final String key;
	private final String url;
	private final CbtRepository cbtRepository;
	private final List<Country> countryList;

	@Autowired
	public CbtHandler(CbtRepository cbtRepository, @Value("${api.url}") String url, @Value("${api.key}") String key) throws IOException, URISyntaxException {
		this.cbtRepository = cbtRepository;
		this.key = key;
		this.url = url;
		List<Country> countryList = new ObjectMapper().readValue(Paths.get(ClassLoader.getSystemResource("country.json").toURI()).toFile(), new TypeReference<>(){});
		log.info("country list : " + countryList);
		this.countryList = Collections.unmodifiableList(countryList);
	}

	public Mono<ServerResponse> getWeatherByLocation(ServerRequest request) {
		double lon = Double.parseDouble(request.queryParam("lon").orElseThrow(IllegalArgumentException::new));
		double lat = Double.parseDouble(request.queryParam("lat").orElseThrow(IllegalArgumentException::new));

		return ServerResponse.ok()
				.build();
	}

	public Mono<ServerResponse> getWeatherByCountry(ServerRequest request) {
		String countryName = request.queryParam("country").orElseThrow(IllegalArgumentException::new);

		return WebClient.builder()
				.build()
				.get()
				.uri(url + "?q=" + countryName + "&appid=" + key)
				.retrieve()
				.bodyToMono(String.class)
				.log()
				.flatMap(s -> ServerResponse.ok().bodyValue(s));
	}

//	private Country findClosestCountryByLocation(double lon, double lat) {
//
//	}

}

