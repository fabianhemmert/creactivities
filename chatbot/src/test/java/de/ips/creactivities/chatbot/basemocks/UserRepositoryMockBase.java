package de.ips.creactivities.chatbot.basemocks;

import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.UserRepository;

import java.util.Optional;

public abstract class UserRepositoryMockBase implements UserRepository {
    @Override
    public <S extends UserEntity> S save(S entity) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public <S extends UserEntity> Iterable<S> saveAll(Iterable<S> entities) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Optional<UserEntity> findById(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public boolean existsById(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Iterable<UserEntity> findAll() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Iterable<UserEntity> findAllById(Iterable<String> strings) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public long count() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void deleteById(String s) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void delete(UserEntity entity) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void deleteAll(Iterable<? extends UserEntity> entities) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void deleteAll() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }
}
