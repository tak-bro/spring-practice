package practice.springboot.gamification.service;

import org.springframework.stereotype.Service;
import practice.springboot.gamification.domain.LeaderBoardRow;
import practice.springboot.gamification.repository.ScoreCardRepository;

import java.util.List;

@Service
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private ScoreCardRepository scoreCardRepository;

    LeaderBoardServiceImpl(ScoreCardRepository scoreCardRepository) {
        this.scoreCardRepository = scoreCardRepository;
    }

    @Override
    public List<LeaderBoardRow> getCurrentLeaderBoard() {
        return scoreCardRepository.findFirst10();
    }

}
