package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
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


    public GameProgress(String userId, String sceneId, String checkpointLocation, String currentPosition, float healthStatus, int deathCount, float[] scenePoints, float[] sceneFinishTimes, Date lastUpdated) {
        this.userId = userId;
        this.sceneId = sceneId;
        this.checkpointLocation = checkpointLocation;
        this.currentPosition = currentPosition;
        this.healthStatus = healthStatus;
        this.deathCount = deathCount;
        this.scenePoints = scenePoints;
        this.sceneFinishTimes = sceneFinishTimes;
        this.lastUpdated = lastUpdated;
    }
}
