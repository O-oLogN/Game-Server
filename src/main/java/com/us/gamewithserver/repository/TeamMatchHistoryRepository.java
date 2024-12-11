package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.TeamMatchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface TeamMatchHistoryRepository extends MongoRepository<TeamMatchHistory, String> {
    @Query("{ 'teamName': ?0 }")
    ArrayList<TeamMatchHistory> findAllByTeamName(String teamName);
}
