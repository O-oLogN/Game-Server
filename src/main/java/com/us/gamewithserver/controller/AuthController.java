package com.us.gamewithserver.controller;

import com.us.gamewithserver.model.PasswordResetToken;
import com.us.gamewithserver.model.Session;
import com.us.gamewithserver.model.User;
import com.us.gamewithserver.payload.GetUserIdBySessionTokenResponse;
import com.us.gamewithserver.payload.LoginRequest;
import com.us.gamewithserver.payload.PasswordChangeRequest;
import com.us.gamewithserver.payload.SessionResponse;
import com.us.gamewithserver.repository.PasswordResetTokenRepository;
import com.us.gamewithserver.repository.SceneRepository;
import com.us.gamewithserver.repository.SessionRepository;
import com.us.gamewithserver.repository.UserRepository;
import com.us.gamewithserver.service.EmailService;
import com.us.gamewithserver.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SessionRepository sessionRepository;

    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private SceneRepository sceneRepository;

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

    @PostMapping("/user-id")
    public ResponseEntity<?> getUserId(@Valid @RequestBody String sessionToken, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        Session session = userService.getUserIdBySessionToken(sessionToken);
        return ResponseEntity.ok(new GetUserIdBySessionTokenResponse(session.getUserId()));
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

            Session session = sessionRepository.findBySessionToken(sessionToken).orElse(null);
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

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@RequestHeader("Session-Token") String sessionToken) {

        try {
            // ensure the session is existed in the database
            Session session = sessionRepository.findBySessionToken(sessionToken).orElse(null);
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

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> passwordResetRequest(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Valid email required!");
        }
        // check email look ok
        // 1. Find the user from the email
        // 2. NO -> Return bad request, YES -> send an email, and return the successful request

        User user = userRepository.findByEmailAddress(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Email not found!");
        }
        // generate random string token
        String resetToken = UUID.randomUUID().toString();
        // create a passwordResetToken and save
        PasswordResetToken passwordResetToken = new PasswordResetToken(resetToken,user.getId(), new Date(System.currentTimeMillis() + 15 * 60 * 1000));
        passwordResetTokenRepository.save(passwordResetToken);
        //send the email
        try {
            emailService.sendPasswordResetEmail(email, resetToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when the server try to send to the email");
        }


        return ResponseEntity.ok("We have sent the reset token to your email!\n" + "Check your inbox");
    }

    @PostMapping("/password-reset")
    public ResponseEntity passwordReset(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("resetToken");
            String newPass = request.get("newPassword");
//            String confirmNewPass = request.get("confirmNewPassword");
            // only check token not password, because I check it in the FE phase and even same password.
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("No reset token found");
            }

            PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElse(null);
            if (passwordResetToken == null) {
                return ResponseEntity.badRequest().body("Invalid reset token");
            }
            // check expiry reset token
            if (passwordResetToken.getExpiryDate().before(new Date())) {
                System.out.println(new Date());
                passwordResetTokenRepository.delete(passwordResetToken);
                return ResponseEntity.badRequest().body("Expiry reset token");
            }


            User user = userRepository.findById(passwordResetToken.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("No user found");
            }

            user.setPassword(newPass);
            user.hashPassword();

            userRepository.save(user);

            passwordResetTokenRepository.delete(passwordResetToken);

            return ResponseEntity.ok().body("Change password successfully");
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }


    }


}
