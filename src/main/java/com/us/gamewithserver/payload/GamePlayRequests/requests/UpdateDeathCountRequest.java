package com.us.gamewithserver.payload.GamePlayRequests.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateDeathCountRequest {
    @NotBlank
    private String userId;
}
