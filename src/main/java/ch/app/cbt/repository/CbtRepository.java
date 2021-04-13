package ch.app.cbt.repository;

import ch.app.cbt.model.CbtInfo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CbtRepository extends R2dbcRepository<CbtInfo, Integer> {
//	@Query("select * from T_CBT_INFO")
//	Flux<CbtInfo> getAll();
}
