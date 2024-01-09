package com.hbc.pms.plc.integration.huykka7;

import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.util.S7;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.plc4x.java.api.value.PlcValue;

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
  private PlcValue plcValue;

  public <T> T getValue() throws S7Exception {
    Object value = null;
    if (plcValue != null){
      switch (this.dateType){
        case BIT -> value = plcValue.getBoolean();
        case D_INTEGER -> value = plcValue.getInteger();
        case DOUBLE -> value = plcValue.getDouble();
        default -> throw new S7Exception(-1, "Not supported data type");
      }
      return (T)value;
    }

    switch (this.dateType){
      case BIT -> value = S7.getBitAt(rawData[0],0);
      case D_INTEGER -> value = S7.getDIntAt(rawData, 0);
      case DOUBLE -> value = S7.getFloatAt(rawData, 0);
      default -> throw new S7Exception(-1, "Not supported data type");
    }
    return (T) value;
  }
}
