package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateSceneFinishTimeRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private int sceneIndex;
    @NotBlank
    private float sceneFinishTime;
}
