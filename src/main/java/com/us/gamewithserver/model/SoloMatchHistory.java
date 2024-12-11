package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "solo_match_history")
@AllArgsConstructor
public class SoloMatchHistory {
    @Id
    private String id;
    private int duration;
    private PlayerStats player1;
    private PlayerStats player2;
    private String startTime;   // dd-mm-yyyy hh:mm
}

@Data
@AllArgsConstructor
class PlayerStats {
    private String userId;
    private String username;
    private boolean won;
    private float points;
    private float damageDealt;
    private float damageTaken;
}
