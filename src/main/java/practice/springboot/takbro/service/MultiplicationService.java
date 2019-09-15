package practice.springboot.takbro.service;

import practice.springboot.takbro.domain.Multiplication;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();
    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);
}
