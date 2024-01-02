package com.hbc.pms.core.api.domain;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Blueprint {
    private String id;
    private String name;
    private String description;
    private List<SensorConfiguration> sensorConfigurations;

    @Getter
    @Setter
    public static class SensorConfiguration {
        private String dataType; // this field will be calculated based on address
        private String offset;// this field will be calculated based on address
        private String dataBlockNumber;// this field will be calculated based on address
        private Point displayCoordinates;
        private String address; //For ex: "DB9.D2060.0", "DB9.D2064.0"
    }

    @Getter
    @Setter
    public static class Point {
        private int x;
        private int y;
    }

    public List<String> getAddresses() {
        return sensorConfigurations.stream().map(SensorConfiguration::getAddress).toList();
    }
}
