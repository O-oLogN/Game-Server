package com.us.gamewithserver.payload.GamePlayRequests.requests;
import lombok.Data;

@Data
public class GetTeamMatchHistoryByUsernameRequest {
    private String username;
}
