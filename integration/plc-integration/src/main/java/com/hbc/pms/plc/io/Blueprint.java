package com.hbc.pms.plc.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hbc.pms.plc.integration.huykka7.DataType;
import com.hbc.pms.plc.integration.huykka7.S7VariableAddress;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return sensorConfigurations.stream().map(SensorConfiguration::getAddress).toList();
    }
}
