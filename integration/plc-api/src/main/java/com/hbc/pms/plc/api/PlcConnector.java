package com.hbc.pms.plc.api;


import java.util.List;
import java.util.Map;

public interface PlcConnector {
    Map<String, IoResponse> executeMultiVarRequest(List<String> variableNames);
    Map<String, IoResponse> executeBlockRequest(List<String> variableNames);
}
