package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@Document(collection = "solo_stats")
public class SoloStats {
    @Id
    private String id;
    private String matchHistory;
    private Float previousPoints;
    private Float totalPoints;
    private String userId;
    private String userName;
}
