package com.us.gamewithserver.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "game_progress")
public class GameProgress {
    @Id
    @Getter
    @Setter
    private String id;
    private String userId;
    private String sceneId;
    private String checkpointLocation;
    private String currentPosition;
    private float healthStatus;
    private int deathCount;
    private float[] scenePoints;
    private float[] sceneFinishTimes;
    private Date lastUpdated;

}
