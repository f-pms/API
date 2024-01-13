package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.api.service.PlcService;
import com.hbc.pms.plc.api.IoResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
public class DataFetcher {
    private final PlcService plcService;

    public DataFetcher(PlcService plcService) {
        this.plcService = plcService;
    }

    public Map<String, IoResponse> fetchData(List<String> addresses)  {
        var nonDuplicatedAddresses = new ArrayList<>(new LinkedHashSet<>(addresses));
        return plcService.getMultiVars(nonDuplicatedAddresses);
    }
}
