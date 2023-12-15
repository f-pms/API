package com.hbc.pms.plc.integration.huykka7;

import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.S7MultiVar;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S7Connector {
  private final ConcurrentMap<String, S7VariableAddress> s7VariableAddresses = new ConcurrentHashMap<>();

  private final PlcConnectionConfiguration plcConnectionConfiguration;

  private S7Client s7Client;

  private final S7VariableNameParser variableNameParser = new S7VariableNameParser();

  public S7Connector(PlcConnectionConfiguration plcConnectionConfiguration) {
    this.plcConnectionConfiguration = plcConnectionConfiguration;
  }

  public boolean connect() throws S7Exception {
    s7Client = new S7Client();
    try {
      s7Client.connect(
          plcConnectionConfiguration.getIpAddress(),
          plcConnectionConfiguration.getRack(),
          plcConnectionConfiguration.getCpuMpiAddress());
      return true;
    } catch (S7Exception e) {
      log.error(e.getMessage(), e);
      //      throw e;
      //      return false;
    }
    return false;
  }

  public Map<String, byte[]> executeMultiVarRequest(List<String> variableNames) throws S7Exception {
    if (variableNames.isEmpty()) {
      return new HashMap<>();
    }
    try(S7MultiVar s7MultiVar = new S7MultiVar(s7Client)){

      List<Map.Entry<String, byte[]>> buffers =
          variableNames.stream()
              .map(
                  key ->
                      new AbstractMap.SimpleEntry<>(
                          key,
                          s7VariableAddresses.computeIfAbsent(key, variableNameParser::parse)))
              .map(
                  x -> {
                    byte[] buffer = new byte[x.getValue().length];
                    s7MultiVar.add(
                        AreaType.DB,
                        DataType.BYTE,
                        x.getValue().dbNr,
                        x.getValue().start,
                        x.getValue().length,
                        buffer);
                    return new AbstractMap.SimpleEntry<>(x.getKey(), buffer);
                  })
              .collect(Collectors.toList());
      boolean result = s7MultiVar.read();
      if (!result) {
        throw new IllegalStateException(
            "Error in MultiVar request for variables: " + String.join(",", variableNames));
      }
      return buffers.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
  }
}
