package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.api.service.PlcService;
import com.hbc.pms.plc.api.IoResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataFetcher {

  private final PlcService plcService;

  public Map<String, IoResponse> fetchData(List<String> addresses) {
    var nonDuplicatedAddresses = new ArrayList<>(new LinkedHashSet<>(addresses));
    log.info("Start fetching total {} addresses", nonDuplicatedAddresses.size());
    if (nonDuplicatedAddresses.isEmpty()) {
      return Map.of();
    }
    return plcService.getMultiVars(nonDuplicatedAddresses);
  }
}
