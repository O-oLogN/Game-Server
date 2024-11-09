package com.us.gamewithserver.payload.GamePlayRequests.responses;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;

@Data
public class PlayerRankingResponse {
    @NonNull
    ArrayList<String> usernameList;
    @NonNull
    ArrayList<Float> finalFinishTimeList;
    @NonNull
    ArrayList<Integer> totalDeathCountList;
    @NonNull
    ArrayList<Float> finalPointsList;
}
