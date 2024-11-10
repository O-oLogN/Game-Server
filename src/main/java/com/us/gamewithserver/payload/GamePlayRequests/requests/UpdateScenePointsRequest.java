package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateScenePointsRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private int sceneIndex;
    @NotBlank
    private float scenePoint;
}
