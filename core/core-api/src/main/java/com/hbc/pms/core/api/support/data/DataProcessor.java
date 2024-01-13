package com.hbc.pms.core.api.support.data;

import com.hbc.pms.plc.api.IoResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataProcessor {
    public Map<String, String> flattenPLCData(Map<String, IoResponse> rawData) {
        Map<String, String> flattenedData = new HashMap<>();

        for (Map.Entry<String, IoResponse> entry : rawData.entrySet()) {
            var value = entry.getValue().getValue().toString();
            if (Float.parseFloat(value) == 0) {
                value = "x";
            }
            flattenedData.put(entry.getKey(), value);
        }
        return flattenedData;
    }
}
