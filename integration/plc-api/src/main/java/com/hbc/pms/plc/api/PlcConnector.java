package com.hbc.pms.plc.api;

import com.hbc.pms.plc.api.exceptions.WritePlcException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.spi.values.PlcIECValue;

public interface PlcConnector {

  void runScheduler();

  void updateScheduler();

  PlcResponseCode write(String address, PlcIECValue<?> value) throws WritePlcException;
}
