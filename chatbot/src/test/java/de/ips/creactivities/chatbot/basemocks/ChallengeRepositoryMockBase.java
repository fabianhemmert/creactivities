package de.ips.creactivities.chatbot.basemocks;

import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;

import java.util.Optional;

public class ChallengeRepositoryMockBase implements ChallengeRepository {
    @Override
    public <S extends ChallengeEntity> S save(S entity) {
        return null;
    }

    @Override
    public <S extends ChallengeEntity> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<ChallengeEntity> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<ChallengeEntity> findAll() {
        return null;
    }

    @Override
    public Iterable<ChallengeEntity> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(ChallengeEntity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends ChallengeEntity> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
