package practice.springboot.gamification.service;

import practice.springboot.gamification.domain.LeaderBoardRow;

import java.util.List;

public interface LeaderBoardService {
    List<LeaderBoardRow> getCurrentLeaderBoard();
}
