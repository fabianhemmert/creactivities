package de.ips.creactivities.chatbot.repo;

import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EvaluationRepository extends CrudRepository<EvaluationEntity, Long> {

    List<EvaluationEntity> findAllByUser(UserEntity user);

}
