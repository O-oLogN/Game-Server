package com.us.gamewithserver.payload;

import lombok.Data;

@Data
public class UpdatePlayerCurrentPositionRequest {
    private String userId;
    private String position;
    private String sceneId;
}
