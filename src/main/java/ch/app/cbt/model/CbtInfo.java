package ch.app.cbt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("T_CBT_INFO")
public class CbtInfo {

    @Id
    @JsonIgnore
    private int id;
    @JsonProperty("id")
    private int countryId;
    private String weatherData;
    private LocalDateTime insertDate;


}
