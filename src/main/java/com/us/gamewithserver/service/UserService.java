package com.us.gamewithserver.service;

import com.us.gamewithserver.model.Session;
import com.us.gamewithserver.model.User;
import com.us.gamewithserver.repository.SessionRepository;
import com.us.gamewithserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private SessionRepository sessionRepository;

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) throws Exception {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username is already taken");
        }
        if (userRepository.existsByEmailAddress(user.getEmailAddress())) {
            throw new Exception("Email is already in use");
        }

        // Hash the password
        user.hashPassword();

        // Save the user
        return userRepository.save(user);
    }

    public User authenticateUser(String usernameOrEmail, String password) throws Exception {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmailAddress(usernameOrEmail).orElse(null));

        if (user == null) {
            throw new Exception("User not found");
        }

        // Check the password
        if (!user.checkPassword(password)) {
            throw new Exception("Invalid password");
        }

        return user;
    }

    public Session createSession(User user) {
        Session session = new Session();
        session.setUserId(user.getId());
        session.setSessionToken(UUID.randomUUID().toString());
        session.setLoginTime(new Date());

        // Save session to database (you'll need a SessionRepository)
        sessionRepository.save(session);

        return session;
    }

}
