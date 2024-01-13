package com.hbc.pms.plc.api;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class S7VariableAddress {
    private AreaType areaType;
    private int dbNr;
    private int start;
    private int length;
    private byte bit;
    private DataType type;
}
