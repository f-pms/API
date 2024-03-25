package com.hbc.pms.plc.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface PlcConnector {

  void runScheduler();

  void updateScheduler();
}
