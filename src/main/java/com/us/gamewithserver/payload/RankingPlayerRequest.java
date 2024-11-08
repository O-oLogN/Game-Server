package com.us.gamewithserver.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RankingPlayerRequest {
    @NotBlank (message = "Please provide userId")
    private String userId;
}
