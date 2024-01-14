package com.hbc.pms.plc.integration.plc4x;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.exceptions.NotSupportedPlcResponseException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.value.PlcValue;
import org.apache.plc4x.java.s7.readwrite.tag.S7Tag;
import org.apache.plc4x.java.spi.messages.DefaultPlcReadResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@Primary
public class Plc4xConnector implements PlcConnector {
    private final PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager().getConnection("s7://35.154.81.136");
    public Plc4xConnector() throws PlcConnectionException {
    }

    private static IoResponse getIoResponse(PlcReadResponse defaultPlcReadResponse, String entry) {
        IoResponse ioResponse = new IoResponse();
        PlcValue plcValue = defaultPlcReadResponse.getPlcValue(entry);
        if (Objects.isNull(plcValue)) {
//            log.error("Tag with address {} is null", entry);
            //TODO: escalate to higher layer to resolve the invalid tags
        }
        ioResponse.setPlcValue(plcValue);
        ioResponse.setVariableName(entry);
        return ioResponse;
    }

    @Override
    @SneakyThrows
    public Map<String, IoResponse> executeBlockRequest(List<String> variableNames) {
        Map<String, IoResponse> stringIoResponseMap = new HashMap<>();
        if (!plcConnection.getMetadata().canRead()) {
            log.error("This connection doesn't support reading.");
            return stringIoResponseMap;
        }
        PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
        for (var address : variableNames) {
            builder.addTag(address, S7Tag.of(address));
        }
        final PlcReadRequest rr = builder.build();
        long startTime = System.currentTimeMillis();
        PlcReadResponse result = rr.execute().get();
        long endTime = System.currentTimeMillis();
        log.info("total:{}", (endTime - startTime));
        if (result instanceof DefaultPlcReadResponse defaultPlcReadResponse) {
            for (var entry : defaultPlcReadResponse.getValues().keySet()) {
                IoResponse ioResponse = getIoResponse(defaultPlcReadResponse, entry);
                stringIoResponseMap.put(entry, ioResponse);
            }
        } else {
            throw new NotSupportedPlcResponseException("PlcReadResponse is not instance of DefaultPlcReadResponse, throwing an exception");
        }
        return stringIoResponseMap;
    }

    @Override
    @SneakyThrows
    public IoResponse validate(String address) {
        PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
        builder.addTag(address, S7Tag.of(address));
        PlcReadResponse readResponse = builder.build().execute().get();
        return getIoResponse(readResponse, address);
    }
}
