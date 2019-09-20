package practice.springboot.gamification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.springboot.gamification.domain.LeaderBoardRow;
import practice.springboot.gamification.service.LeaderBoardService;

import java.util.List;

@RestController
@RequestMapping("leaders")
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    public LeaderBoardController(final LeaderBoardService leaderBoardService) {
        this.leaderBoardService = leaderBoardService;
    }

    @GetMapping
    public List<LeaderBoardRow> getLeaderBoard() {
        return this.leaderBoardService.getCurrentLeaderBoard();
    }

}
