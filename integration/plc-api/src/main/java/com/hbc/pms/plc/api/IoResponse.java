package com.hbc.pms.plc.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.plc4x.java.api.model.PlcTag;
import org.apache.plc4x.java.api.value.PlcValue;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IoResponse {
  private String variableName;
    private PlcTag plcTag;
  private PlcValue plcValue;
}
