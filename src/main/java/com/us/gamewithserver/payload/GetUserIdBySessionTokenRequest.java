package com.us.gamewithserver.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GetUserIdBySessionTokenRequest {
    @NotBlank
    private String sessionToken;
}
