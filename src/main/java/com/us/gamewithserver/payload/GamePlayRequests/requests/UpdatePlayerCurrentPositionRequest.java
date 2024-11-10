package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePlayerCurrentPositionRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String position;
    @NotBlank
    private int sceneIndex;
}
