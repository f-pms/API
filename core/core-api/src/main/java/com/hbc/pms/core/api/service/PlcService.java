package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import com.hbc.pms.plc.integration.mokka7.util.S7;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PlcService {

  private final S7Client plcClient;

  @Value("${hbc.plc.url}")
  private String plcUrl;

  public PlcService(S7Client plcClient) {
    this.plcClient = plcClient;
  }

  // TODO: add retries in case the PLC connection is interrupted
  @PostConstruct
  public void postConstruct() {
    try {
      plcClient.connect(plcUrl, 0, 1);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean readBoolean(int dbNumber, int startByte) throws S7Exception {
    return plcClient.readBit(AreaType.DB, dbNumber, startByte, 0);
  }

  public int readInt(int dbNumber, int startByte) throws S7Exception {
    return plcClient.readInt(AreaType.DB, dbNumber, startByte);
  }

  public float readFloat(int dbNumber, int startByte) throws S7Exception {
    final byte[] buffer = new byte[1024];
    plcClient.readArea(AreaType.DB, dbNumber, startByte, 1, DataType.REAL, buffer);
    return S7.getFloatAt(buffer, 0);
  }

}
