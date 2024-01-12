package com.hbc.pms.core.api.support.data;

import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataProcessor {
    public Map<String, String> flattenPLCData(Map<String, IoResponse> rawData) {
        Map<String, String> flattenedData = new HashMap<>();

        for (Map.Entry<String, IoResponse> entry : rawData.entrySet()) {
            try {
                var value = entry.getValue().getValue().toString();
                if (Float.parseFloat(value) == 0) {
                    value = "x";
                }
                flattenedData.put(entry.getKey(), value);
            } catch (S7Exception e) {
                if (e.getErrorCode() == -1) {
                    flattenedData.put(entry.getKey(), "x");
                }
            }
        }
        return flattenedData;
    }
}
