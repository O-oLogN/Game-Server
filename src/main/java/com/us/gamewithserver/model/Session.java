package com.us.gamewithserver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "sessions")
public class Session {

    @Id
    private String id;

    private String userId;
    private String sessionToken;
    private Date loginTime;
    private Date logoutTime;

}
