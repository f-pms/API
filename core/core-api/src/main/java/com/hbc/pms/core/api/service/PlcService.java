package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.block.S7DataItem;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import com.hbc.pms.plc.integration.mokka7.util.S7;
import com.hbc.pms.plc.io.Coordinate;
import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlcService {

  private final S7Client plcClient = new S7Client();
  @Value("${hbc.plc.url}")
  private String plcUrl;

  @PostConstruct
  public void postConstruct() {
    // TODO: add retries in case the PLC connection is interrupted
    try {
      plcClient.connect(plcUrl, 0, 1);
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

  @Scheduled(fixedRate = 250)
  public void testMultiVars() throws S7Exception {
    var s7Items = new S7DataItem[100];
    s7Items[0] = new S7DataItem(AreaType.DB, DataType.BIT, 1, 4, 1);
    s7Items[1] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 6, 1);
    s7Items[2] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 10, 1);
    s7Items[3] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 14, 1);
    s7Items[4] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 18, 1);
    s7Items[5] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 22, 1);
    s7Items[6] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 26, 1);
    s7Items[7] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 30, 1);
    s7Items[8] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 34, 1);
    s7Items[9] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 38, 1);
    s7Items[10] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 42, 1);
    s7Items[11] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 46, 1);
    s7Items[12] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 50, 1);
    s7Items[13] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 54, 1);
    s7Items[14] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 58, 1);
    s7Items[15] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 62, 1);
    s7Items[16] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 66, 1);
    s7Items[17] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 70, 1);
    s7Items[18] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 74, 1);
    s7Items[19] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 78, 1);
    s7Items[20] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 82, 1);
    s7Items[21] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 86, 1);
    s7Items[22] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 90, 1);
    s7Items[23] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 94, 1);
    s7Items[24] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 98, 1);
    s7Items[25] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 102, 1);
    s7Items[26] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 106, 1);
    s7Items[27] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 110, 1);
    s7Items[28] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 114, 1);
    s7Items[29] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 118, 1);
    s7Items[30] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 122, 1);
    s7Items[31] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 126, 1);
    s7Items[32] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 130, 1);
    s7Items[33] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 134, 1);
    s7Items[34] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 138, 1);
    s7Items[35] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 142, 1);
    s7Items[36] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 146, 1);
    s7Items[37] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 150, 1);
    s7Items[38] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 154, 1);
    s7Items[39] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 158, 1);
    s7Items[40] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 162, 1);
    s7Items[41] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 166, 1);
    s7Items[42] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 170, 1);
    s7Items[43] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 174, 1);
    s7Items[44] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 178, 1);
    s7Items[45] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 182, 1);
    s7Items[46] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 186, 1);
    s7Items[47] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 190, 1);
    s7Items[48] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 194, 1);
    s7Items[49] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 198, 1);
    s7Items[50] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 202, 1);
    s7Items[51] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 206, 1);
    s7Items[52] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 210, 1);
    s7Items[53] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 214, 1);
    s7Items[54] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 218, 1);
    s7Items[55] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 222, 1);
    s7Items[56] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 226, 1);
    s7Items[57] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 230, 1);
    s7Items[58] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 234, 1);
    s7Items[59] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 238, 1);
    s7Items[60] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 242, 1);
    s7Items[61] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 246, 1);
    s7Items[62] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 250, 1);
    s7Items[63] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 254, 1);
    s7Items[64] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 258, 1);
    s7Items[65] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 262, 1);
    s7Items[66] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 266, 1);
    s7Items[67] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 270, 1);
    s7Items[68] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 274, 1);
    s7Items[69] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 278, 1);
    s7Items[70] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 282, 1);
    s7Items[71] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 286, 1);
    s7Items[72] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 290, 1);
    s7Items[73] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 294, 1);
    s7Items[74] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 298, 1);
    s7Items[75] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 302, 1);
    s7Items[76] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 306, 1);
    s7Items[77] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 310, 1);
    s7Items[78] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 314, 1);
    s7Items[79] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 318, 1);
    s7Items[80] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 322, 1);
    s7Items[81] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 326, 1);
    s7Items[82] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 330, 1);
    s7Items[83] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 334, 1);
    s7Items[84] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 338, 1);
    s7Items[85] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 342, 1);
    s7Items[86] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 346, 1);
    s7Items[87] = new S7DataItem(AreaType.DB, DataType.DINT, 1, 350, 1);
    s7Items[88] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 354, 1);
    s7Items[89] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 358, 1);
    s7Items[90] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 362, 1);
    s7Items[91] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 366, 1);
    s7Items[92] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 370, 1);
    s7Items[93] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 374, 1);
    s7Items[94] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 378, 1);
    s7Items[95] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 382, 1);
    s7Items[96] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 386, 1);
    s7Items[97] = new S7DataItem(AreaType.DB, DataType.REAL, 1, 390, 1);
    plcClient.readMultiVars(Arrays.copyOfRange(s7Items, 0, 17) , 16);
    log.info("item: {}", S7.getFloatAt(s7Items[1].data,0));
  }

}
