package com.us.gamewithserver.payload;

import lombok.Data;

@Data
public class UpdateCheckpointLocationRequest {
    private String userId;
    private String location;
    private String sceneId;
}
