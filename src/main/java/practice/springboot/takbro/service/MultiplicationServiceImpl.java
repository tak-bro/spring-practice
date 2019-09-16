package practice.springboot.takbro.service;

import org.springframework.util.Assert;
import practice.springboot.takbro.domain.Multiplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

    private RandomGeneratorService randomGeneratorService;

    @Autowired
    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService) {
        this.randomGeneratorService = randomGeneratorService;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();
        return new Multiplication(factorA, factorB);
    }

    @Override
    public boolean checkAttempt(final MultiplicationResultAttempt attempt) {
        boolean isCorrect = attempt.getResultAttempt() == attempt.getMultiplication().getFactorA() * attempt.getMultiplication().getFactorB();
        Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다.");

        MultiplicationResultAttempt checkAttempt = new MultiplicationResultAttempt(attempt.getUser(), attempt.getMultiplication(), attempt.getResultAttempt(), isCorrect);
        return isCorrect;
    }
}
