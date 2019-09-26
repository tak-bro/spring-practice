package practice.springboot.gamification.client;

import practice.springboot.gamification.client.dto.MultiplicationResultAttempt;

public interface MultiplicationResultAttemptClient {
    MultiplicationResultAttempt retrieveMultiplicationResultAttemptById(final Long multiplicationId);
}
