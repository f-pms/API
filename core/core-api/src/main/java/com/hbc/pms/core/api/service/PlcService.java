package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.integration.huykka7.PlcConnectionConfiguration;
import com.hbc.pms.plc.integration.huykka7.S7Connector;
import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import com.hbc.pms.plc.io.Coordinate;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
@Slf4j
public class PlcService {

  private final S7Client plcClient = new S7Client();
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
      //      plcClient.connect(plcUrl, 0, 1);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean readBoolean(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, DataType.BIT);
    return plcClient.readBit(AreaType.DB, coordinate.getDb(), coordinate.getStartByte(), 0);
  }

  public int readInt(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, DataType.DINT);
    return plcClient.readInt(AreaType.DB, coordinate.getDb(), coordinate.getStartByte());
  }

  public float readFloat(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, DataType.REAL);
    return plcClient.readFloat(AreaType.DB, coordinate.getDb(), coordinate.getStartByte());
  }

  private void validateType(Coordinate coordinate, DataType type) {
    var typeAsString = type.name().toLowerCase(Locale.ENGLISH);
    if (!Objects.equals(coordinate.getType(), typeAsString)) {
      throw new RuntimeException(String.format("Read value type is not expected to be a %s value", typeAsString));
    }
  }

  @Scheduled(fixedRate = 1500)
  public void testMultiVars() throws S7Exception {
    long startTime = System.currentTimeMillis();
    var map =
        s7Connector.executeMultiVarRequest(
            Arrays.asList(
                "DB1.DINT14",
                "DB1.DINT18",
                "DB1.DINT22",
                "DB1.DINT26",
                "DB1.DINT30",
                "DB1.DINT34",
                "DB1.DINT38",
                "DB1.DINT42",
                "DB1.DINT46",
                "DB1.D50",
                "DB1.D54",
                "DB1.D58",
                "DB1.D62",
                "DB1.D66",
                "DB1.D70",
                "DB1.D74",
                "DB1.D78",
                "DB1.D82",
                "DB1.D86",
                "DB1.DINT90",
                "DB1.DINT94",
                "DB1.DINT98",
                "DB1.DINT102",
                "DB1.DINT106",
                "DB1.DINT110",
                "DB1.DINT114",
                "DB1.DINT118",
                "DB1.DINT122",
                "DB1.D126",
                "DB1.D130",
                "DB1.D134",
                "DB1.D138",
                "DB1.D142",
                "DB1.D146",
                "DB1.D150",
                "DB1.D154",
                "DB1.D158",
                "DB1.D162",
                "DB1.DINT166",
                "DB1.DINT170",
                "DB1.DINT174",
                "DB1.DINT178",
                "DB1.DINT182",
                "DB1.DINT186",
                "DB1.DINT190",
                "DB1.DINT194",
                "DB1.DINT198",
                "DB1.D202",
                "DB1.D206",
                "DB1.D210",
                "DB1.D214",
                "DB1.D218",
                "DB1.D222",
                "DB1.D226",
                "DB1.D230",
                "DB1.D234",
                "DB1.D238",
                "DB1.DINT242",
                "DB1.DINT246",
                "DB1.DINT250",
                "DB1.DINT254",
                "DB1.DINT258",
                "DB1.DINT262",
                "DB1.DINT266",
                "DB1.DINT270",
                "DB1.DINT274",
                "DB1.D278",
                "DB1.D282",
                "DB1.D286",
                "DB1.D290",
                "DB1.D294",
                "DB1.D298",
                "DB1.D302"));
    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);
    log.info("Execution time: " + duration + " milliseconds");
  }

}
