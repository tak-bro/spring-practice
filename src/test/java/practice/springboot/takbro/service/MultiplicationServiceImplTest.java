package practice.springboot.takbro.service;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import practice.springboot.takbro.domain.Multiplication;
import practice.springboot.takbro.domain.MultiplicationResultAttempt;
import practice.springboot.takbro.domain.User;
import practice.springboot.takbro.event.EventDispatcher;
import practice.springboot.takbro.event.MultiplicationSolvedEvent;
import practice.springboot.takbro.repository.MultiplicationResultAttemptRepository;
import practice.springboot.takbro.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;

public class MultiplicationServiceImplTest {

    private MultiplicationServiceImpl multiplicationServiceImpl;

    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;

    @Mock
    private EventDispatcher eventDispatcher;

    @Before
    public void setUp() {
        // 목 객체를 초기화합니다.
        MockitoAnnotations.initMocks(this);
        multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService, multiplicationResultAttemptRepository, userRepository, eventDispatcher);
    }

    @Test
    public void createRandomMultiplicationTest() {
        // given (randomGeneratorService 가 처음에 50, 나중에 30을 반환하도록 설정)
        given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

        // when
        Multiplication multiplication = multiplicationServiceImpl.createRandomMultiplication();

        // assert
        assertThat(multiplication.getFactorA()).isEqualTo(50);
        assertThat(multiplication.getFactorB()).isEqualTo(30);
    }

    @Test
    public void checkCorrectAttemptTest() {
        // given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John_doe");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);
        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, true);
        MultiplicationSolvedEvent multiplicationSolvedEvent = new MultiplicationSolvedEvent(attempt.getId(), attempt.getUser().getId(), true);
        given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

        boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

        assertThat(attemptResult).isTrue();
        verify(multiplicationResultAttemptRepository).save(verifiedAttempt);

        // event test
        verify(eventDispatcher).send(eq(multiplicationSolvedEvent));
    }

    @Test
    public void checkWrongAttemptTest() {
        // given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John_doe");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationSolvedEvent multiplicationSolvedEvent = new MultiplicationSolvedEvent(attempt.getId(), attempt.getUser().getId(), false);
        given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

        boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

        assertThat(attemptResult).isFalse();
        verify(multiplicationResultAttemptRepository).save(attempt);
        // event test
        verify(eventDispatcher).send(eq(multiplicationSolvedEvent));
    }

    @Test
    public void retrieveStatsTest() {
        // given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John_doe");

        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3051, false);

        List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);

        given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());
        given(multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc("John_doe")).willReturn(latestAttempts);

        // when
        List<MultiplicationResultAttempt> latestAttemptsResult = multiplicationServiceImpl.getStatsForUser("John_doe");

        // then
        assertThat(latestAttemptsResult).isEqualTo(latestAttempts);
    }
}