package practice.springboot.takbro.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import practice.springboot.takbro.domain.Multiplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;
import practice.springboot.takbro.domain.User;
import practice.springboot.takbro.event.EventDispatcher;
import practice.springboot.takbro.event.MultiplicationSolvedEvent;
import practice.springboot.takbro.repository.MultiplicationResultAttemptRepository;
import practice.springboot.takbro.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

    private RandomGeneratorService randomGeneratorService;
    private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;
    private UserRepository userRepository;
    private EventDispatcher eventDispatcher;

    @Autowired
    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                     MultiplicationResultAttemptRepository multiplicationResultAttemptRepository,
                                     UserRepository userRepository,
                                     EventDispatcher eventDispatcher) {
        this.randomGeneratorService = randomGeneratorService;
        this.multiplicationResultAttemptRepository = multiplicationResultAttemptRepository;
        this.userRepository = userRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();
        return new Multiplication(factorA, factorB);
    }

    @Transactional
    @Override
    public boolean checkAttempt(final MultiplicationResultAttempt attempt) {
        // check user
        Optional<User> user = userRepository.findByAlias(attempt.getUser().getAlias());
        Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다.");

        boolean isCorrect = attempt.getResultAttempt() == attempt.getMultiplication().getFactorA() * attempt.getMultiplication().getFactorB();

        MultiplicationResultAttempt checkAttempt = new MultiplicationResultAttempt(
                user.orElse(attempt.getUser()),
                attempt.getMultiplication(),
                attempt.getResultAttempt(),
                isCorrect
        );

        // 답안 저장
        multiplicationResultAttemptRepository.save(checkAttempt);

        // 이벤트 전송
        MultiplicationSolvedEvent multiplicationSolvedEvent = new MultiplicationSolvedEvent(checkAttempt.getId(),checkAttempt.getUser().getId(), checkAttempt.isCorrect());
        eventDispatcher.send(multiplicationSolvedEvent);

        return isCorrect;
    }

    @Override
    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
        return multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
    }

    @Override
    public MultiplicationResultAttempt getResultById(Long resultId) {
        return multiplicationResultAttemptRepository.findById(resultId).get();
    }

}
