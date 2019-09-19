package practice.springboot.takbro.repository;

import org.springframework.data.repository.CrudRepository;
import practice.springboot.takbro.domain.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByAlias(final String alias);
}
