package com.us.gamewithserver.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "scenes")
public class Scene {
    @Id
    private String id;
    private String name;
}
