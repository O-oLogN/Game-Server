package com.us.gamewithserver.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "player_rankings")
public class PlayerRanking implements Comparable<PlayerRanking> {
    @Id
    private String id;
    private String userId;
    private float finalPoints;
    private Integer rank;
    private int totalDeaths;
    private float finishTime;

    public PlayerRanking(String userId, float finalPoints, Integer rank, int totalDeaths, float finishTime) {
        this.userId = userId;
        this.finalPoints = finalPoints;
        this.rank = rank;
        this.totalDeaths = totalDeaths;
        this.finishTime = finishTime;
    }

    @Override
    public int compareTo(PlayerRanking o) {
        if (this.finalPoints != o.finalPoints) return Float.compare(this.finalPoints, o.finalPoints);
        if (this.finishTime != o.finishTime) return Float.compare(this.finishTime, o.finishTime);
        if (this.totalDeaths != o.totalDeaths) return this.totalDeaths - o.totalDeaths;
        return this.userId.compareTo(o.userId);
    }
}
