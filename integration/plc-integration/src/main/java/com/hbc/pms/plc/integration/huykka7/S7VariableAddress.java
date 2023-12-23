package com.hbc.pms.plc.integration.huykka7;

import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class S7VariableAddress {
  public AreaType areaType;
  public int dbNr;
  public int start;
  public int length;
  public byte bit;
  public DataType type;
}
