package com.hbc.pms.plc.api;

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

  public <T> T getValue() {
    Object value = null;
      switch (this.dateType){
        case BIT -> value = plcValue.getBoolean();
        case D_INTEGER -> value = plcValue.getInteger();
        case DOUBLE -> value = plcValue.getDouble();
        default -> throw new RuntimeException("Not supported data type");
      }
      return (T)value;
  }
}
