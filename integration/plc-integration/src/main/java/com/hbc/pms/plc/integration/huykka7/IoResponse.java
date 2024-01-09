package com.hbc.pms.plc.integration.huykka7;

import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.util.S7;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IoResponse {
  public IoResponse(String variableName, DataType dateType, byte[] rawData) {
    this.variableName = variableName;
    this.dateType = dateType;
    this.rawData = rawData;
  }

  private String variableName;
  private DataType dateType;
  private byte[] rawData;
  private Object value;

  public <T> T getValue() throws S7Exception {
    if (this.value != null) return (T) value;
    Object value = null;
    switch (this.dateType){
      case BIT -> value = S7.getBitAt(rawData[0],0);
      case D_INTEGER -> value = S7.getDIntAt(rawData, 0);
      case DOUBLE -> value = S7.getFloatAt(rawData, 0);
      default -> throw new S7Exception(-1, "Not supported data type");
    }
    return (T) value;
  }
}
