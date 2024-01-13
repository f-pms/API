package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.PlcConnectionException;
import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.huykka7.PlcConnectionConfiguration;
import com.hbc.pms.plc.integration.huykka7.S7Connector;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PlcService {

    private S7Connector s7Connector;
    @Value("${hbc.plc.url}")
    private String plcUrl;

    @PostConstruct
    public void postConstruct() {
        // TODO: add retries in case the PLC connection is interrupted
        try {
            PlcConnectionConfiguration plcConnectionConfiguration =
                PlcConnectionConfiguration.builder()
                    .ipAddress(plcUrl)
                    .rack(0)
                    .cpuMpiAddress(1)
                    .build();

            s7Connector = new S7Connector(plcConnectionConfiguration);
            s7Connector.connect();
        } catch (S7Exception e) {
            throw new PlcConnectionException("Error connecting to PLC: " + e);
        }
    }

    public Map<String, IoResponse> getMultiVars(List<String> addresses) throws S7Exception {
        return s7Connector.executeMultiVarRequest(addresses);
    }
}
