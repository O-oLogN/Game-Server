package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.PlayerRanking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRankingRepository extends MongoRepository<PlayerRanking, String> {
    Optional<PlayerRanking> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
