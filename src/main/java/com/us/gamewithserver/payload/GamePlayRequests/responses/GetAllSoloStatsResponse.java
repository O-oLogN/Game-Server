package com.us.gamewithserver.payload.GamePlayRequests.responses;

import com.us.gamewithserver.model.SoloStats;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GetAllSoloStatsResponse {
    private ArrayList<SoloStats> soloStatsList;
}

