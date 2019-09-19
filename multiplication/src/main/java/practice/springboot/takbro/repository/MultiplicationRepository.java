package practice.springboot.takbro.repository;

import org.springframework.data.repository.CrudRepository;
import practice.springboot.takbro.domain.Multiplication;

public interface MultiplicationRepository extends CrudRepository<Multiplication, Long> {
}
