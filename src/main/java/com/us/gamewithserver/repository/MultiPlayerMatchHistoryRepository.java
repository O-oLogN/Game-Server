package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.MultiPlayerMatchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultiPlayerMatchHistoryRepository extends MongoRepository<MultiPlayerMatchHistory, String> {
}
