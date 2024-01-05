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

    public Map<String, String> flattenToFigureMappedData(Map<String, IoResponse> rawData, Blueprint blueprint) throws S7Exception {
        Map<String, String> flattenedData = new HashMap<>();
        var addressToFigureMap = blueprint.getAddressToFiguresMap();

        for (Map.Entry<String, IoResponse> entry : rawData.entrySet()) {
            var valuesByAddress = addressToFigureMap.get(entry.getKey());

            for (String val : valuesByAddress) {
                flattenedData.put(val, entry.getValue().getValue().toString());
            }
        }
        return flattenedData;
    }
}
