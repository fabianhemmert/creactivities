package de.ips.creactivities.chatbot.basemocks;

import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SolutionRepositoryBaseMock implements SolutionRepository {

    @Override
    public List<SolutionEntity> findAllByUserIsNotAndChallengeIsIn(UserEntity user, Collection<ChallengeEntity> challenges) {
        return null;
    }

    @Override
    public List<SolutionEntity> findAllByChallengeAndUser(ChallengeEntity challenge, UserEntity user) {
        return null;
    }

    @Override
    public List<SolutionEntity> findAllByUser(UserEntity user) {
        return null;
    }

    @Override
    public <S extends SolutionEntity> S save(S entity) {
        return null;
    }

    @Override
    public <S extends SolutionEntity> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<SolutionEntity> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<SolutionEntity> findAll() {
        return null;
    }

    @Override
    public Iterable<SolutionEntity> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(SolutionEntity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends SolutionEntity> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
