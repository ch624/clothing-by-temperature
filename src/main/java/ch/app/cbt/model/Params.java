package ch.app.cbt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Params {
    private long lon;
    private long lat;
    private int cityId;
    private String cityName;
    private int postCode;
}
