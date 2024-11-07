package com.us.gamewithserver.payload;

import lombok.Data;

@Data
public class SessionResponse {
    private String sessionToken;

    public SessionResponse(String sessionToken) {
        this.sessionToken = sessionToken;
    }


}
