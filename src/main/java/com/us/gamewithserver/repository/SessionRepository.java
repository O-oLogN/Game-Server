package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessionRepository extends MongoRepository<Session, String> {
    Optional<Session> findBySessionToken(String sessionToken);
}
