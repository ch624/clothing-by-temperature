package ch.app.cbt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    private int id;
    private String name;
    private String country;
    private double lon;
    private double lat;

    public void setCoord(Map<String, String> map) {
        this.lon = Double.parseDouble(map.get("lon"));
        this.lat = Double.parseDouble(map.get("lat"));
    }
}
