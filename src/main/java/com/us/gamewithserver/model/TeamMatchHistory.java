package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document (collection = "team_match_history")
public class TeamMatchHistory {
    @Id
    private String id;
    private int duration;
    private String startTime;
    private TeamMember[] teamMembers;
    private String teamName;
    private int teamSize;
}
