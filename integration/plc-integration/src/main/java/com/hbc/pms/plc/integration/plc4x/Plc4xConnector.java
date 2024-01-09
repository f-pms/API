package com.hbc.pms.plc.integration.plc4x;

import com.hbc.pms.plc.PlcConnector;
import com.hbc.pms.plc.integration.huykka7.DataType;
import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.huykka7.S7VariableAddress;
import com.hbc.pms.plc.integration.huykka7.S7VariableNameParser;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@Primary

public class Plc4xConnector implements PlcConnector {
    private final S7VariableNameParser variableNameParser = new S7VariableNameParser();
    private final PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager().getConnection("s7://35.154.81.136");

    public Plc4xConnector() throws PlcConnectionException {
    }

    @Override
    public Map<String, IoResponse> executeMultiVarRequest(List<String> variableNames) throws S7Exception {
        return this.executeBlockRequest(variableNames);
    }

    @Override
    @SneakyThrows
    public Map<String, IoResponse> executeBlockRequest(List<String> variableNames) {
        Map<String, S7VariableAddress> addressMap = getSimpleEntryStream(variableNames);
        Map<String, IoResponse> stringIoResponseMap = new HashMap<>();
        if (!plcConnection.getMetadata().canRead()) {
            log.error("This connection doesn't support reading.");
            return stringIoResponseMap;
        }
        PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

        for (var entry : addressMap.entrySet()) {
            builder.addTagAddress(entry.getKey(), convertToPLx4x(entry.getValue()));
        }
        final PlcReadRequest rr = builder.build();
        PlcReadResponse result = rr.execute().get();
        for (var entry : addressMap.entrySet()) {
            var data = result.getObject(entry.getKey());
            var io = new IoResponse(entry.getKey(), entry.getValue().getType(), serialize(data));
            io.setPlcValue(result.getPlcValue(entry.getKey()));
            stringIoResponseMap.put(entry.getKey(), io);
        }
        return stringIoResponseMap;
    }

    private String convertToPLx4x(S7VariableAddress s7VariableAddress) {
        return MessageFormat.format("%DB{0}:{1}:{2}", s7VariableAddress.getDbNr(), String.valueOf(s7VariableAddress.getStart()), convertType(s7VariableAddress.getType()));
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private String convertType(DataType type) {
        switch (type) {
            case DOUBLE -> {
                return "DWORD";
            }
        }
        return "DWORD";
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

}
