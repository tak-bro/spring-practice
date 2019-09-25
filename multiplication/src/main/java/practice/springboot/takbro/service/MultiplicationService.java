package practice.springboot.takbro.service;

import practice.springboot.takbro.domain.Multiplication;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;

import java.util.List;
import java.util.Optional;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();
    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);
    List<MultiplicationResultAttempt> getStatsForUser(String userAlias);
    MultiplicationResultAttempt getResultById(Long resultId);

}
