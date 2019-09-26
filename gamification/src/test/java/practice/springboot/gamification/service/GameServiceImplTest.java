package practice.springboot.gamification.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.BDDMockito.given;
import practice.springboot.gamification.domain.Badge;
import practice.springboot.gamification.domain.BadgeCard;
import practice.springboot.gamification.domain.GameStats;
import practice.springboot.gamification.domain.ScoreCard;
import practice.springboot.gamification.repository.BadgeCardRepository;
import practice.springboot.gamification.repository.ScoreCardRepository;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

public class GameServiceImplTest {

    private GameServiceImpl gameService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Mock
    private BadgeCardRepository badgeCardRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gameService = new GameServiceImpl(scoreCardRepository, badgeCardRepository);
    }

    @Test
    public void processFirstCorrectAttemptTest() {
        // given
        Long userId = 1L;
        Long attemptId = 8L;
        int totalScore = 10;

        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        // 이 리파지토리는 방금 얻은 점수 카드를 반환
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(Collections.singletonList(scoreCard));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.emptyList());

        // when
        GameStats iteration = gameService.newAttemptForUser(userId, attemptId, true);

        // assert - 점수 카드 하나와 첫 번째 정답 배지를 획득
        assertThat(iteration.getScore()).isEqualTo(scoreCard.getScore());
        assertThat(iteration.getBadges()).containsOnly(Badge.FIRST_WON);
    }

}
