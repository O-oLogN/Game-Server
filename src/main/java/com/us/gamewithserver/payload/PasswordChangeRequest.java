package com.us.gamewithserver.payload;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class PasswordChangeRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Min(6)
    private String newPassword;

    @NotBlank
    @Min(6)
    private String confirmPassword;
}
