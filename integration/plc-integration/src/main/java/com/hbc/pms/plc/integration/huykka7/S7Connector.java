package com.hbc.pms.plc.integration.huykka7;

import static com.hbc.pms.plc.integration.mokka7.Client.MAX_VARS;

import com.hbc.pms.core.model.TrackExecutionTime;
import com.hbc.pms.plc.PlcConnector;
import com.hbc.pms.plc.integration.huykka7.block.S7BlockRequest;
import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.S7MultiVar;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import jakarta.annotation.PostConstruct;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class S7Connector implements PlcConnector {
  private final PlcConnectionConfiguration plcConnectionConfiguration;
  private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();
  private S7Client s7Client;

  private final S7VariableNameParser variableNameParser = new S7VariableNameParser();

  public S7Connector(PlcConnectionConfiguration plcConnectionConfiguration) {
    this.plcConnectionConfiguration = plcConnectionConfiguration;
  }

  @PostConstruct
  public boolean connect() throws S7Exception {
    readLock.lock();
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
    } finally {
      readLock.unlock();
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
  public Map<String, IoResponse> executeBlockRequest(List<String> variableNames) {
    if (variableNames.isEmpty()) {
      return new HashMap<>();
    }
    Map<String, S7VariableAddress> mapOfVars = getSimpleEntryStream(variableNames);
    Map<Integer, S7BlockRequest> blockRequest = convertVarsToBlockRequest(mapOfVars);
    return getIoResponse(blockRequest);
  }

  public static <T> Stream<List<T>> batches(List<T> source, int length) {
    if (length <= 0) throw new IllegalArgumentException("length = " + length);
    int size = source.size();
    if (size <= 0) return Stream.empty();
    int fullChunks = (size - 1) / length;
    return IntStream.range(0, fullChunks + 1)
        .mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
  }

  private Map<String, IoResponse> getIoResponse(  Map<Integer, S7BlockRequest> blockRequest) {

    for (Map.Entry<Integer, S7BlockRequest> entry : blockRequest.entrySet()) {
      readLock.lock();
      try {
        long startTime =System.currentTimeMillis();
        s7Client.readArea(
            AreaType.DB,
            entry.getKey(),
            entry.getValue().getOffset(),
            entry.getValue().getEnd() - entry.getValue().getOffset(),
            DataType.BYTE,
            entry.getValue().getBuffer());
        long stopTime =System.currentTimeMillis();
        log.info("amount of bytes read: {}", entry.getValue().getEnd() - entry.getValue().getOffset());
        log.info("Execution time: " + (stopTime - startTime) + " milliseconds");
      } catch (S7Exception e) {
        log.error("Error in BlockRequest for variables: {}", String.join(",", entry.getValue().getVars().keySet()));
        log.error(e.getMessage(), e);
      } finally {
        readLock.lock();
      }

    }
    return blockRequest.values().stream()
        .map(s7BlockRequest -> s7BlockRequest.getResult(variableNameParser))
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, IoResponse> getStringIoResponseMap(List<String> variableNames)
      throws S7Exception {
    Map<String, S7VariableAddress> mapOfVars = getSimpleEntryStream(variableNames);
    readLock.lock();
    try(S7MultiVar s7MultiVar = new S7MultiVar(s7Client)){
      List<Map.Entry<String, byte[]>> buffers =
          mapOfVars.entrySet().stream()
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
    } finally {
      readLock.unlock();
    }
  }

  private Map<Integer, S7BlockRequest> convertVarsToBlockRequest(
      Map<String, S7VariableAddress> stringS7VariableAddressMap) {
    Map<Integer, S7BlockRequest> result = new HashMap<>();
    for (Map.Entry<String, S7VariableAddress> entry : stringS7VariableAddressMap.entrySet()) {
      S7BlockRequest s7BlockRequest =
          result.computeIfAbsent(
              entry.getValue().getDbNr(), k -> new S7BlockRequest(entry.getValue().getDbNr()));
      s7BlockRequest.getVars().put(entry.getKey(), entry.getValue());
      int offset = s7BlockRequest.getOffset();
      int end = s7BlockRequest.getEnd();
      if (entry.getValue().getStart() < offset) {
        offset = entry.getValue().getStart();
      }

      if (entry.getValue().getStart() + entry.getValue().getLength() > end) {
        end = entry.getValue().getStart() + entry.getValue().getLength();
      }
      s7BlockRequest.setOffset(offset);
      s7BlockRequest.setEnd(end);
    }
    return result;
  }

  private Map<String, S7VariableAddress> getSimpleEntryStream(List<String> variableNames) {
    final ConcurrentMap<String, S7VariableAddress> s7VariableAddresses = new ConcurrentHashMap<>();

    return variableNames.stream()
        .map(
            key ->
                new AbstractMap.SimpleEntry<>(
                    key, s7VariableAddresses.computeIfAbsent(key, variableNameParser::parse)))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private IoResponse mapToIo(Map.Entry<String, byte[]> entry) {
    return new IoResponse(
        entry.getKey(), variableNameParser.parse(entry.getKey()).getType(), entry.getValue());
  }
}
