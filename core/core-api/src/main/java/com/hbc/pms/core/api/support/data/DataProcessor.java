package com.hbc.pms.core.api.support.data;

import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataProcessor {
    public Map<String, String> flattenPLCData(Map<String, IoResponse> rawData) throws S7Exception {
        Map<String, String> flattenedData = new HashMap<>();

        for (Map.Entry<String, IoResponse> entry : rawData.entrySet()) {
            var entryValue = entry.getValue();
            flattenedData.put(entry.getKey(), entry.getValue().getValue().toString());
        }
        return flattenedData;
    }
}
