package ch.app.cbt.handler;

import ch.app.cbt.common.exceptions.CountryNotFoundException;
import ch.app.cbt.model.CbtInfo;
import ch.app.cbt.model.Country;
import ch.app.cbt.repository.CbtRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class CbtHandler {
    private final String key;
    private final String url;
    private final CbtRepository cbtRepository;
    private final List<Country> countryList;
    private final ObjectMapper objectMapper;

    @Autowired
    public CbtHandler(CbtRepository cbtRepository, @Value("${api.url}") String url, @Value("${api.key}") String key, ObjectMapper objectMapper) throws IOException, URISyntaxException {
        this.cbtRepository = cbtRepository;
        this.objectMapper = objectMapper;
        this.key = key;
        this.url = url;
        List<Country> countryList = new ObjectMapper().readValue(new ClassPathResource("city.list.json").getInputStream(), new TypeReference<>() {});
        this.countryList = Collections.unmodifiableList(countryList);
    }

    public Mono<ServerResponse> getWeatherByLocation(ServerRequest request) {
        return Mono.just(request)
                .map(serverRequest -> {
                    double lon = Double.parseDouble(serverRequest.queryParam("lon").orElseThrow(IllegalArgumentException::new));
                    double lat = Double.parseDouble(serverRequest.queryParam("lat").orElseThrow(IllegalArgumentException::new));
                    return findClosestCountryByLocation(lon, lat);
                })
                .flatMap(country -> cbtRepository.findByCountryId(country.getId()).switchIfEmpty(getCountyAndSave(country)))
                .flatMap(cbtInfo -> cbtInfo.getInsertDate().plusMinutes(30).isBefore(LocalDateTime.now()) ? getCountyAndSave(cbtInfo) : Mono.just(cbtInfo))
                .flatMap(cbtInfo -> ServerResponse.ok().bodyValue(cbtInfo));
    }

    @Transactional
    public Mono<CbtInfo> getCountyAndSave(CbtInfo cbtInfo) {
        return WebClient.builder()
                .build()
                .get()
                .uri(url + "?id=" + cbtInfo.getCountryId() + "&appid=" + key)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(s -> {
                    cbtInfo.setWeatherData(s);
                    cbtInfo.setInsertDate(LocalDateTime.now());
                    return cbtRepository.save(cbtInfo);
                });
    }

    public Mono<CbtInfo> getCountyAndSave(Country country) {
        return WebClient.builder()
                .build()
                .get()
                .uri(url + "?lon=" + country.getLon() + "&lat=" + country.getLat() + "&appid=" + key)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(s -> Mono.fromCallable(() -> {
                    CbtInfo cbtInfo = objectMapper.readValue(s, CbtInfo.class);
                    cbtInfo.setWeatherData(s);
                    cbtInfo.setInsertDate(LocalDateTime.now());
                    return cbtInfo;
                }).subscribeOn(Schedulers.boundedElastic()))
                .flatMap(cbtRepository::save);
    }

//    public Mono<ServerResponse> getWeatherByCountry(ServerRequest request) {
//        String countryName = request.queryParam("country").orElseThrow(IllegalArgumentException::new);
//
//        return WebClient.builder()
//                .build()
//                .get()
//                .uri(url + "?q=" + countryName + "&appid=" + key)
//                .retrieve()
//                .bodyToMono(String.class)
//                .log()
//                .flatMap(s -> ServerResponse.ok().bodyValue(s));
//    }

    private Country findClosestCountryByLocation(double lon, double lat) {
        Country closestCountry = countryList
                .stream()
                .findAny()
                .orElseThrow(CountryNotFoundException::new);
        double sum = Math.abs(lon - closestCountry.getLon()) + Math.abs(lat - closestCountry.getLat());
        for (Country country : countryList) {
            double tempSum = Math.abs(lon - country.getLon()) + Math.abs(lat - country.getLat());
            if (sum > tempSum) {
                closestCountry = country;
                sum = tempSum;
            }
        }

        return closestCountry;
    }

}

