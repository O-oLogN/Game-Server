package com.us.gamewithserver.payload.GamePlayRequests.responses;

import com.us.gamewithserver.model.TeamMatchHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GetTeamMatchHistoryByTeamNameResponse {
    private ArrayList<TeamMatchHistory> teamMatchHistoryList;
}
