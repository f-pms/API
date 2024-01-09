package com.hbc.pms.plc.integration.huykka7.block;

import com.hbc.pms.plc.integration.huykka7.IoResponse;
import com.hbc.pms.plc.integration.huykka7.S7VariableAddress;
import com.hbc.pms.plc.integration.huykka7.S7VariableNameParser;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S7BlockRequest {

  public S7BlockRequest(int dataBlockNumber) {
    this.dataBlockNumber = dataBlockNumber;
    this.vars = new HashMap<>();
    this.offset = 999999;
    this.end = 0;
    this.buffer = new byte[30000];
  }
  private int offset;
  private int end;

  private int dataBlockNumber;
  private Map<String, S7VariableAddress> vars;
  private byte[] buffer;


  public Map<String,IoResponse> getResult(S7VariableNameParser s7VariableNameParser){
      Map<String, IoResponse> result = new HashMap<>();
    for (Map.Entry<String, S7VariableAddress> entry : vars.entrySet()) {
      String key = entry.getKey();
      S7VariableAddress value = entry.getValue();
      byte[] bytes = new byte[value.getLength()];
      System.arraycopy(buffer, value.getStart(), bytes, 0, value.getLength());
      IoResponse ioResponse = new IoResponse(
          entry.getKey(), s7VariableNameParser.parse(entry.getKey()).getType(), bytes);
      result.put(key, ioResponse);
    }
    return result; }

}
