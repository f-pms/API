package com.hbc.pms.plc.api;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface PlcConnector {
    Map<String, IoResponse> executeBlockRequest(List<String> variableNames);

    IoResponse validate(String address) throws ExecutionException, InterruptedException;

    void runScheduler();

    void updateScheduler();
}
