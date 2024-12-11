package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

@Data
public class GetSoloMatchHistoryByUsernameRequest {
    private String username;
}
