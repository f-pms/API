package com.hbc.pms.core.api.support.json;

import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TempSerializer {
    public static String serialize(Map<String, IoResponse> obj) throws S7Exception {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, IoResponse> entry : obj.entrySet()) {
            var val = entry.getValue();
            list.add(val.getVariableName() + ":" + val.getValue(entry.getClass()));
        }


        return list.toString();
    }
}
