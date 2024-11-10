package com.us.gamewithserver.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.us.gamewithserver.model.GameProgress;
import com.us.gamewithserver.model.PlayerRanking;
import com.us.gamewithserver.model.Session;
import com.us.gamewithserver.model.User;
import com.us.gamewithserver.repository.GameProgressRepository;
import com.us.gamewithserver.repository.PlayerRankingRepository;
import com.us.gamewithserver.payload.LoginRequest;
import com.us.gamewithserver.repository.SessionRepository;
import com.us.gamewithserver.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {


    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private GameProgressRepository gameProgressRepository;
    @Autowired
    private PlayerRankingRepository playerRankingRepository;

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
        User savedUser = userRepository.save(user);

        // Create new document in game_progress, player_rankings collections
        this.gameProgressRepository.save(new GameProgress(
                savedUser.getId(),
                "",
                "",
                "",
                1,
                0,
                new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
                new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
                null
        ));

        this.playerRankingRepository.save(new PlayerRanking(
                savedUser.getId(),
                0,
                null,
                0,
                0
        ));

        return savedUser;
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



    public String changePassword(String userId, String currentPassword, String newPassword) throws Exception {
        // get user from session

        // Retrieve the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Verify the current password
        if (!user.checkPassword(currentPassword)) {
            throw new Exception("Current password is incorrect");
        }

        // Hash the new password
        user.setPassword(newPassword);
        user.hashPassword();

        // Save the updated user
        userRepository.save(user);

        return "Password changes successfully!";
    }

    public Session getUserIdBySessionToken(String sessionToken) throws Exception {
        Session session = sessionRepository.findBySessionToken(sessionToken)
                .orElseGet(() -> sessionRepository.findBySessionToken(sessionToken).orElse(null));
        if (session == null) {
            throw new Exception("Session not found");
        }
        return session;
    }
}
