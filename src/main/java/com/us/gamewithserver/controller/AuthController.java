package com.us.gamewithserver.controller;

import com.us.gamewithserver.model.Session;
import com.us.gamewithserver.model.User;
import com.us.gamewithserver.payload.GetUserIdBySessionTokenResponse;
import com.us.gamewithserver.payload.LoginRequest;
import com.us.gamewithserver.payload.SessionResponse;
import com.us.gamewithserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Sign-Up Endpoint
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        try {
            if (result.hasErrors()) {
                // Collect validation errors
                String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
                return ResponseEntity.badRequest().body(errorMessage);
            }

            // Register the user
            User newUser = userService.registerUser(user);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
                return ResponseEntity.badRequest().body(errorMessage);
            }

            User user = userService.authenticateUser(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

            // Create session
            Session session = userService.createSession(user);

            // Return session token to client
            return ResponseEntity.ok(new SessionResponse(session.getSessionToken()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/user-id")
    public ResponseEntity<?> getUserId(@Valid @RequestBody String sessionToken, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        Session session = userService.getUserIdBySessionToken(sessionToken);
        return ResponseEntity.ok(new GetUserIdBySessionTokenResponse(session.getUserId()));
    }
}
