package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GameContinueRequest {
    @NotBlank (message = "Please provide userId")
    private String userId;
}
