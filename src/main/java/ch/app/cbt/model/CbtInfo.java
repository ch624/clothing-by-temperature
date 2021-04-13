package ch.app.cbt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CbtInfo {
    @Id
    private int id;

    @JsonProperty("cod")
    private int code;

    
}
