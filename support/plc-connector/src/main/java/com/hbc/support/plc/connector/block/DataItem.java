package com.hbc.support.plc.connector.block;

import com.hbc.support.plc.connector.type.AreaType;
import com.hbc.support.plc.connector.type.DataType;

public class DataItem {

    public AreaType area;
    public DataType type;
    public int db;
    public int start;
    public int bitPos;

    public DataItem(AreaType area, DataType type, int db, int start) {
        this(area, type, db, start, 0);
    }

    public DataItem(AreaType area, DataType type, int db, int start, int bitPos) {
        this.area = area;
        this.type = type;
        this.db = db;
        this.start = start;
        this.bitPos = bitPos;
    }

    @Override
    public String toString() {
        return "DataItem [" + (area != null ? "area=" + area + ", " : "") + (type != null ? "type=" + type + ", " : "") + "db=" + db + ", start=" + start + "]";
    }

}

