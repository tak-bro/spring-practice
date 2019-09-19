package practice.springboot.takbro.repository;

import org.springframework.data.repository.CrudRepository;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;

import java.util.List;

public interface MultiplicationResultAttemptRepository extends CrudRepository<MultiplicationResultAttempt, Long> {
    List<MultiplicationResultAttempt> findTop5ByUserAliasOrderByIdDesc(String userAlias);
}
