package com.hbc.pms.plc.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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
        private Point displayCoordinate;
        @Setter
        private String address; //For ex: "DB9.D2060.0", "DB9.D2064.0"
    }

    @Getter
    @Setter
    public static class Point {
        private double x;
        private double y;
    }

    public Map<String, List<String>> getAddressToFiguresMap() {
        var flattenedMap =sensorConfigurations.stream()
            .flatMap(sensorConfiguration -> sensorConfiguration.figures.stream());
        var result = new HashMap<String, List<String>>();
        flattenedMap.forEach(figure -> result.computeIfAbsent(figure.getAddress(), k -> new ArrayList<>()).add(figure.getId()));

        return result;
    }

    public List<String> getAddresses() {
        return sensorConfigurations.stream()
            .flatMap(sensorConfiguration -> sensorConfiguration.figures.stream())
            .map(Figure::getAddress)
            .collect(Collectors.toList());
    }
}
