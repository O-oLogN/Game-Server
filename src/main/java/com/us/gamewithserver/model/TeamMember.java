package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMember {
    private String userId;
    private String username;
    private int deathCount;
}
