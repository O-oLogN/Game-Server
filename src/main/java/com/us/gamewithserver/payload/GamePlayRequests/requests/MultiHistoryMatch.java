package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;
import java.util.List;

@Data
public class MultiHistoryMatch {
    private Double timeElapsed;
    private String dateStartTime;
    private List<String> playerNames;
    private Integer deathCount;
}
