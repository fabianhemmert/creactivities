package de.ips.creactivities.chatbot.repo;

import de.ips.creactivities.chatbot.dm.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {

}
