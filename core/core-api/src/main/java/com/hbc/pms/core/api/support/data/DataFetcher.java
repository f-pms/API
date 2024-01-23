package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.api.service.PlcService;
import com.hbc.pms.plc.api.IoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataFetcher {
    private final PlcService plcService;

    public Map<String, IoResponse> fetchData(List<String> addresses)  {
        var nonDuplicatedAddresses = new ArrayList<>(new LinkedHashSet<>(addresses));
        log.info("Start fetching total {} addresses", nonDuplicatedAddresses.size());
        return plcService.getMultiVars(nonDuplicatedAddresses);
    }
}
