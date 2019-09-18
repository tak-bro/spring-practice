package practice.springboot.takbro.service;

import practice.springboot.takbro.domain.Multiplication;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;

import java.util.List;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();
    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);
    List<MultiplicationResultAttempt> getStatsForUser(String userAlias);

}
