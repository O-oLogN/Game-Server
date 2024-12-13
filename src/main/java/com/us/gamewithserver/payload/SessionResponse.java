package com.us.gamewithserver.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SessionResponse {
    private String sessionToken;
    private String alias;

    public SessionResponse(String sessionToken, String alias) {
        this.sessionToken = sessionToken;
        this.alias= alias;
    }

}
