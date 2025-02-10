package com.us.gamewithserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "multi_player_match_history")
public class MultiPlayerMatchHistory {
    private String teamName;
}
