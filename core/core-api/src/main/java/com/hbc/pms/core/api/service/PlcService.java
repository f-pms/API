package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PlcService {
    private final PlcConnector s7Connector;

    public Map<String, IoResponse> getMultiVars(List<String> addresses) {
        return s7Connector.executeBlockRequest(addresses);
    }

    public IoResponse validate(String address) {
        return s7Connector.validate(address);
    }
}
