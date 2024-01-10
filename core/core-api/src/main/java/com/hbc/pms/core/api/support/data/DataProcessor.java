package com.hbc.pms.core.api.support.data;

import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.io.Blueprint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataProcessor {
    public Map<String, String> flattenPLCData(Map<String, IoResponse> rawData) throws S7Exception {
        Map<String, String> flattenedData = new HashMap<>();

        for (Map.Entry<String, IoResponse> entry : rawData.entrySet()) {
            flattenedData.put(entry.getKey(), entry.getValue().getValue().toString());
        }
        return flattenedData;
    }

    public Map<String, Map<String, String>> flattenToFigureMappedData(Map<String, IoResponse> rawData,
                                                                      Blueprint blueprint) throws S7Exception {
        Map<String, Map<String, String>> result = new HashMap<>();
        int count = 0;
        for (Blueprint.SensorConfiguration sensorConfig : blueprint.getSensorConfigurations()) {
            Map<String, String> flattenedData = new HashMap<>();
            for (Blueprint.Figure figure : sensorConfig.getFigures()) {
                flattenedData.put(figure.getId(), rawData.get(figure.getAddress()).getValue().toString());
                count++;
            }
            result.put(sensorConfig.getGroupId(), flattenedData);
        }

        System.out.println(count);
        return result;
    }
}
