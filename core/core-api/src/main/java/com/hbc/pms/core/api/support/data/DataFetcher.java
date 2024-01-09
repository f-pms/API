package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.api.service.PlcService;
import com.hbc.pms.core.model.TrackExecutionTime;
import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
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

    @TrackExecutionTime
    public Map<String, IoResponse> fetchData(List<String> addresses)  {
        var nonDuplicatedAddresses = new ArrayList<>(new LinkedHashSet<>(addresses));
        return plcService.getMultiVars(nonDuplicatedAddresses);
    }
}
