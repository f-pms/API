package com.hbc.pms.plc;

import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;

import java.util.List;
import java.util.Map;

public interface PlcConnector {
    Map<String, IoResponse> executeMultiVarRequest(List<String> variableNames)  throws S7Exception;
    Map<String, IoResponse> executeBlockRequest(List<String> variableNames);
}
