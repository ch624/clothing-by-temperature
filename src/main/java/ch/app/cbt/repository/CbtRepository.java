package ch.app.cbt.repository;

import ch.app.cbt.model.CbtInfo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CbtRepository extends R2dbcRepository<CbtInfo, Integer> {
    Mono<CbtInfo> findByCountryId(final int id);
}
