package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.SoloMatchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SoloMatchHistoryRepository extends MongoRepository<SoloMatchHistory, String> {
    @Query("{ $or: [ { 'player1.username': ?0 }, { 'player2.username': ?0 } ] }")
    ArrayList<SoloMatchHistory> findAllByUsername(String username);
}
