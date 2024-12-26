package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

@Data
public class SoloResultRequest {
    private String winner;
    private String loser;
}
