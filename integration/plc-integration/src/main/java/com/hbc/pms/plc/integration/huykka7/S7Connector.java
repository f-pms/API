package com.hbc.pms.plc.integration.huykka7;

import static com.hbc.pms.plc.integration.mokka7.Client.MAX_VARS;

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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
          plcConnectionConfiguration.getPort(),
          plcConnectionConfiguration.getRack(),
          plcConnectionConfiguration.getCpuMpiAddress());
      return true;
    } catch (S7Exception e) {
      log.error(e.getMessage(), e);
    }
    return false;
  }

  public Map<String, IoResponse> executeMultiVarRequest(List<String> variableNames)
      throws S7Exception {
    if (variableNames.isEmpty()) {
      return new HashMap<>();
    }
    List<List<String>> batchesList = batches(variableNames, MAX_VARS).toList();
    Map<String, IoResponse> result = new HashMap<>();
    for (List<String> strings : batchesList) {
      result.putAll(getStringIoResponseMap(strings));
    }
    return result;
  }

  public static <T> Stream<List<T>> batches(List<T> source, int length) {
    if (length <= 0) throw new IllegalArgumentException("length = " + length);
    int size = source.size();
    if (size <= 0) return Stream.empty();
    int fullChunks = (size - 1) / length;
    return IntStream.range(0, fullChunks + 1)
        .mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
  }

  private Map<String, IoResponse> getStringIoResponseMap(List<String> variableNames)
      throws S7Exception {
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
                    byte[] buffer = new byte[x.getValue().getLength()];
                    s7MultiVar.add(
                        AreaType.DB,
                        DataType.BYTE,
                        x.getValue().getDbNr(),
                        x.getValue().getStart(),
                        x.getValue().getLength(),
                        buffer);
                    return new AbstractMap.SimpleEntry<>(x.getKey(), buffer);
                  })
              .collect(Collectors.toList());
      boolean result = s7MultiVar.read();
      if (!result) {
        throw new IllegalStateException(
            "Error in MultiVar request for variables: " + String.join(",", variableNames));
      }
      return buffers.stream()
          .map(this::mapToIo)
          .collect(Collectors.toMap(IoResponse::getVariableName, Function.identity()));
    }
  }

  private IoResponse mapToIo(Map.Entry<String, byte[]> entry) {
    return new IoResponse(
        entry.getKey(), variableNameParser.parse(entry.getKey()).getType(), entry.getValue());
  }
}
