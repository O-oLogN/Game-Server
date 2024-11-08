package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.GameProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameProgressRepository extends MongoRepository<GameProgress, String> {
    Optional<GameProgress> findByUserId(String userId);
}
