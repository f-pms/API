package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlcService {
    private final PlcConnector s7Connector;

    @Value("${hbc.plc.url}")
    private String plcUrl;

    public PlcService(PlcConnector s7Connector) {
        this.s7Connector = s7Connector;
    }

    public Map<String, IoResponse> getMultiVars(List<String> addresses) {
        return s7Connector.executeBlockRequest(addresses);
    }
}
