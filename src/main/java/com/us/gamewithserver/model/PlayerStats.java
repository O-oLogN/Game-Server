package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerStats {
    private String userId;
    private String username;
    private boolean won;
    private float points;
    private float damageDealt;
    private float damageTaken;
}