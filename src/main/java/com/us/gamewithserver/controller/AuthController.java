package com.us.gamewithserver.controller;

import com.us.gamewithserver.model.Session;
import com.us.gamewithserver.model.User;
import com.us.gamewithserver.payload.LoginRequest;
import com.us.gamewithserver.payload.PasswordChangeRequest;
import com.us.gamewithserver.payload.SessionResponse;
import com.us.gamewithserver.repository.SessionRepository;
import com.us.gamewithserver.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SessionRepository sessionRepository;

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
            userService.registerUser(user);

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

    @PostMapping("/change-password")
    public ResponseEntity<?> passwordChange(@RequestHeader("Session-Token") String sessionToken, @Valid @RequestBody PasswordChangeRequest passwordChangeRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
                return ResponseEntity.badRequest().body(errorMessage);
            }

            if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("New password and confirmation do not match");
            }

            Session session = sessionRepository.findBySessionToken(sessionToken);
            if (session == null || session.getLogoutTime() != null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session token");
            }

            String userId = session.getUserId();


            //
            userService.changePassword(userId, passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());

            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("signout")
    public ResponseEntity<?> signOut(@RequestHeader("Session-Token") String sessionToken) {

        try {
            // ensure the session is existed in the database
            Session session = sessionRepository.findBySessionToken(sessionToken);
            if (session == null || session.getLogoutTime() != null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated!");
            }
            // set Log out time
            session.setLogoutTime(new Date());
            sessionRepository.save(session);
            // send the result
            return ResponseEntity.ok("Sign out successfully!");
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body("Error happens when sign out action happens");
        }
    }


}
