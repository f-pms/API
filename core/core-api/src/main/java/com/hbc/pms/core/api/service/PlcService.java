package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.PlcConnectionException;
import com.hbc.pms.plc.PlcConnector;
import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.huykka7.PlcConnectionConfiguration;
import com.hbc.pms.plc.integration.huykka7.S7Connector;
import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import com.hbc.pms.plc.io.Blueprint;
import jakarta.annotation.PostConstruct;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlcService {

    private final S7Client plcClient = new S7Client();

    private final PlcConnector s7Connector;
    @Value("${hbc.plc.url}")
    private String plcUrl;

  public PlcService(PlcConnector s7Connector) {
    this.s7Connector = s7Connector;
  }

//  @PostConstruct
//    public void postConstruct() {
//        // TODO: add retries in case the PLC connection is interrupted
//        try {
////            PlcConnectionConfiguration plcConnectionConfiguration =
////                PlcConnectionConfiguration.builder()
////                    .ipAddress(plcUrl)
////                    .rack(0)
////                    .cpuMpiAddress(1)
////                    .build();
////
////            s7Connector = new S7Connector(plcConnectionConfiguration);
////            s7Connector.connect();
//        } catch (S7Exception e) {
//            throw new PlcConnectionException("Error connecting to PLC: " + e);
//        }
//    }

    public boolean readBoolean(Blueprint.Figure figure) throws S7Exception {
        validateType(figure, DataType.BIT);
        return plcClient.readBit(AreaType.DB, figure.getDataBlockNumber(), figure.getOffset(), 0);
    }

    public int readInt(Blueprint.Figure figure) throws S7Exception {
        validateType(figure, DataType.DINT);
        return plcClient.readInt(AreaType.DB, figure.getDataBlockNumber(), figure.getOffset());
    }

    public float readFloat(Blueprint.Figure figure) throws S7Exception {
        validateType(figure, DataType.REAL);
        return plcClient.readFloat(AreaType.DB, figure.getDataBlockNumber(), figure.getOffset());
    }

    private void validateType(Blueprint.Figure figure, DataType type) {
        var typeAsString = type.name().toLowerCase(Locale.ENGLISH);
        if (!Objects.equals(figure.getDataType(), typeAsString)) {
            throw new UnsupportedOperationException(String.format("Read value type is not expected to be a %s value", typeAsString));
        }
    }

    public Map<String, IoResponse> getMultiVars(List<String> addresses)  {
        return s7Connector.executeBlockRequest(addresses);
    }

//    @Scheduled(fixedRate = 1500)
    public void testMultiVars() throws S7Exception {
        long startTime = System.currentTimeMillis();
        var map =
            s7Connector.executeMultiVarRequest(
                Arrays.asList("DB112.D0.0"));
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        log.info(map.toString());
        log.info("Execution time: " + duration + " milliseconds");
    }
}
