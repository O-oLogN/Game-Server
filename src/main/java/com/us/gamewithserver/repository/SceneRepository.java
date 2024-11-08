package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.Scene;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SceneRepository extends MongoRepository<Scene, String> {
    Optional<Scene> findByName(String sceneName);
}
