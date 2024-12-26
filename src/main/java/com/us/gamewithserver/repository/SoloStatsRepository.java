package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.SoloStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoloStatsRepository extends MongoRepository<SoloStats, String> {
    Optional<SoloStats> findByUserName(String userName);
}
