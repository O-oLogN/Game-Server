package com.us.gamewithserver.repository;

import com.us.gamewithserver.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, String> {

}
