package com.us.gamewithserver.model;

import lombok.Data;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20)
    private String username;

    // Add a method to hash the password before saving
    @Setter
    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Email address is required")
    @Email(message = "Email should be valid")
    private String emailAddress;

    private int points = 0;
    private int status = 1;

    // Getters and setters
    // ...

    public void hashPassword() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt(10));
    }

    // Add a method to check the password
    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }
}
