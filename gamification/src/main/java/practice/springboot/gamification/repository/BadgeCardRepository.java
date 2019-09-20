package practice.springboot.gamification.repository;

import org.springframework.data.repository.CrudRepository;
import practice.springboot.gamification.domain.BadgeCard;

import java.util.List;

public interface BadgeCardRepository extends CrudRepository<BadgeCard, Long> {
    List<BadgeCard> findByUserIdOrderByBadgeTimestampDesc(final Long userId);
}
