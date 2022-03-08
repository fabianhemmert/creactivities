package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.basemocks.UserRepositoryMockBase;
import de.ips.creactivities.chatbot.dm.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockUserRepo extends UserRepositoryMockBase {
    Map<String, UserEntity> users;

    public MockUserRepo(Map<String, UserEntity> users) {
        this.users = users;
    }

    @Override
    public Optional<UserEntity> findById(String s) {
        return (Optional.ofNullable(users.get(s)));
    }

    @Override
    public Iterable<UserEntity> findAllById(Iterable<String> strings) {
        List<UserEntity> toReturn = new ArrayList<>();
        for (String id : strings) {
            if (users.containsKey(id)) {
                toReturn.add(users.get(id));
            }
        }
        return toReturn;
    }

    @Override
    public <S extends UserEntity> S save(S entity) {
        this.users.put(entity.getId(), entity);
        return entity;
    }
}
