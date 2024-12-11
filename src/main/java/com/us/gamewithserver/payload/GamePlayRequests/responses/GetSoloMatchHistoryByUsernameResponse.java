package com.us.gamewithserver.payload.GamePlayRequests.responses;

import com.us.gamewithserver.model.SoloMatchHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GetSoloMatchHistoryByUsernameResponse {
    private ArrayList<SoloMatchHistory> matchHistoryList;
}
