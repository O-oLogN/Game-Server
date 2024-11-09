package com.us.gamewithserver.payload.GamePlayRequests.responses;

import lombok.Data;
import lombok.NonNull;

@Data
public class GameContinueResponse {
    @NonNull
    String sceneName;
    @NonNull
    String currentPosition;
}
