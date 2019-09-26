package practice.springboot.gamification.service;

import org.springframework.stereotype.Service;
import practice.springboot.gamification.client.MultiplicationResultAttemptClient;
import practice.springboot.gamification.client.dto.MultiplicationResultAttempt;
import practice.springboot.gamification.domain.Badge;
import practice.springboot.gamification.domain.BadgeCard;
import practice.springboot.gamification.domain.GameStats;
import practice.springboot.gamification.domain.ScoreCard;
import practice.springboot.gamification.repository.BadgeCardRepository;
import practice.springboot.gamification.repository.ScoreCardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    public static final int LUCKY_NUMBER = 42;

    private ScoreCardRepository scoreCardRepository;
    private BadgeCardRepository badgeCardRepository;
    private MultiplicationResultAttemptClient attemptClient;

    GameServiceImpl(ScoreCardRepository scoreCardRepository,
                    BadgeCardRepository badgeCardRepository,
                    MultiplicationResultAttemptClient attemptClient) {
        this.scoreCardRepository = scoreCardRepository;
        this.badgeCardRepository = badgeCardRepository;
        this.attemptClient = attemptClient;
    }


    @Override
    public GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct) {
        if (!correct) {
            return GameStats.emptyStats(userId);
        }

        // 정답인 경우
        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        scoreCardRepository.save(scoreCard);

        List<BadgeCard> badgeCards = processForBadges(userId, attemptId);
        return new GameStats(userId, scoreCard.getScore(), badgeCards.stream().map(BadgeCard::getBadge).collect(Collectors.toList()));
    }

    @Override
    public GameStats retrieveStatsForUser(Long userId) {
        int score = scoreCardRepository.getTotalScoreForUser(userId);
        List<BadgeCard> badgeCards = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);
        return new GameStats(userId, score, badgeCards.stream().map(BadgeCard::getBadge).collect(Collectors.toList()));
    }

    private List<BadgeCard> processForBadges(Long userId, Long attemptId) {
        List<BadgeCard> badgeCards = new ArrayList<>();

        int totalScore = scoreCardRepository.getTotalScoreForUser(userId);
        List<ScoreCard> scoreCardList = scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId);
        List<BadgeCard> badgeCardList = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);

        // 점수 기반 배지
        this.checkAndGiveBadgeBasedOnScore(badgeCardList, Badge.BRONZE_MULTIPLICATOR, totalScore, 100, userId).ifPresent(badgeCards::add);
        this.checkAndGiveBadgeBasedOnScore(badgeCardList, Badge.SILVER_MULTIPLICATOR, totalScore, 500, userId).ifPresent(badgeCards::add);
        this.checkAndGiveBadgeBasedOnScore(badgeCardList, Badge.GOLD_MULTIPLICATOR, totalScore, 999, userId).ifPresent(badgeCards::add);

        boolean isFirstWon = scoreCardList.size() == 1 && !this.containsBadge(badgeCardList, Badge.FIRST_WON);
        if (isFirstWon) {
            BadgeCard firstWonBadge = this.giveBadgeToUser(Badge.FIRST_WON, userId);
            badgeCards.add(firstWonBadge);
        }

        // 행운의 숫자 배지
        MultiplicationResultAttempt attempt = attemptClient.retrieveMultiplicationResultAttemptById(attemptId);
        if (!containsBadge(badgeCardList, Badge.LUCKY_NUMBER) && (LUCKY_NUMBER == attempt.getMultiplicationFactorA()
                || LUCKY_NUMBER == attempt.getMultiplicationFactorB())) {
            BadgeCard luckyNumberBadge = giveBadgeToUser(Badge.LUCKY_NUMBER, userId);
            badgeCards.add(luckyNumberBadge);
        }

        return badgeCards;
    }

    /**
     * 배지를 얻기 위한 조건을 넘는지 체크하는 편의성 메소드
     * 또한 조건이 충족되면 사용자에게 배지를 부여
     */
    private Optional<BadgeCard> checkAndGiveBadgeBasedOnScore(
            final List<BadgeCard> badgeCards, final Badge badge,
            final int score, final int scoreThreshold, final Long userId) {
        if (score >= scoreThreshold && !containsBadge(badgeCards, badge)) {
            return Optional.of(giveBadgeToUser(badge, userId));
        }
        return Optional.empty();
    }

    /**
     * 배지 목록에 해당 배지가 포함되어 있는지 확인하는 메소드
     */
    private boolean containsBadge(final List<BadgeCard> badgeCards, final Badge badge) {
        return badgeCards.stream().anyMatch(b -> b.getBadge().equals(badge));
    }

    /**
     * 주어진 사용자에게 새로운 배지를 부여하는 메소드
     */
    private BadgeCard giveBadgeToUser(final Badge badge, final Long userId) {
        BadgeCard badgeCard = new BadgeCard(userId, badge);
        badgeCardRepository.save(badgeCard);
        return badgeCard;
    }

}
