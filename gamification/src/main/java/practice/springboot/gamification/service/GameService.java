package practice.springboot.gamification.service;

import practice.springboot.gamification.domain.GameStats;

public interface GameService {
    GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct);
    GameStats retrieveStatsForUser(Long userId);
}
