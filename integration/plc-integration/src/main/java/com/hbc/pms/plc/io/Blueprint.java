package com.hbc.pms.plc.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Blueprint {
    private String id;
    private String name;
    private String description;
    private List<SensorConfiguration> sensorConfigurations;

    @Getter
    public static class SensorConfiguration {
        private String groupId;
        private List<Figure> figures;

    }

    @Getter
    public static class Figure {
        private String id;
        // TODO: auto calculate these 3 fields based on address
        private String dataType;
        private int offset;
        private int dataBlockNumber;
        @Setter
        private Point displayCoordinates;
        @Setter
        private String address; //For ex: "DB9.D2060.0", "DB9.D2064.0"
    }

    @Getter
    @Setter
    public static class Point {
        private double x;
        private double y;
    }

    public List<String> getAddresses() {
        return sensorConfigurations.stream()
            .flatMap(sensorConfiguration -> sensorConfiguration.figures.stream())
            .map(Figure::getAddress)
            .collect(Collectors.toList());
    }
}
