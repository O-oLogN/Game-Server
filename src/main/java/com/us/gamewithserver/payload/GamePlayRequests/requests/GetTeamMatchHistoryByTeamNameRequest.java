package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

@Data
public class GetTeamMatchHistoryByTeamNameRequest {
    private String teamName;
}
