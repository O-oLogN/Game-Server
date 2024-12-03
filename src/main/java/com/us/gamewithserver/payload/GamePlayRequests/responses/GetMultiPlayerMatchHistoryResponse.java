package com.us.gamewithserver.payload.GamePlayRequests.responses;

import com.us.gamewithserver.model.MultiPlayerMatchHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GetMultiPlayerMatchHistoryResponse {
    private ArrayList<MultiPlayerMatchHistory> multiPlayerMatchHistoryList;
}
