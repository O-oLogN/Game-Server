package com.us.gamewithserver.payload;

import lombok.Data;
import lombok.NonNull;

@Data
public class GetUserIdBySessionTokenResponse {
    @NonNull
    private String userId;
}
