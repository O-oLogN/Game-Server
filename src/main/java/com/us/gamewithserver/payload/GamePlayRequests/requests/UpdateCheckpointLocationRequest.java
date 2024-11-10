package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateCheckpointLocationRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String checkpointLocation;
    @NotBlank
    private int sceneIndex;
}
