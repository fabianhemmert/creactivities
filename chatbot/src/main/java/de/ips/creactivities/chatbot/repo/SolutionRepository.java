package de.ips.creactivities.chatbot.repo;

import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface SolutionRepository extends CrudRepository<SolutionEntity, Long> {

    List<SolutionEntity> findAllByUserIsNotAndChallengeIsIn(UserEntity user, Collection<ChallengeEntity> challenges);

    List<SolutionEntity> findAllByChallengeAndUser(ChallengeEntity challenge, UserEntity user);

    List<SolutionEntity> findAllByUser(UserEntity user);

}
