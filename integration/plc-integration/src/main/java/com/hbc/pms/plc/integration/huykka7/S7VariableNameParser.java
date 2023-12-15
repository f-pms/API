package com.hbc.pms.plc.integration.huykka7;

import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import java.util.*;
import java.util.regex.*;
import org.springframework.stereotype.Component;

@Component
public class S7VariableNameParser {
  private final Pattern regex =
      Pattern.compile(
          "^(?<operand>db{1})(?<dbNr>\\d{1,4})\\.?(?<type>dbx|x|s|string|b|dbb|d|int|dbw|w|dint|dul|dulint|dulong|){1}(?<start>\\d+)(\\.(?<bitOrLength>\\d+))?$",
          Pattern.CASE_INSENSITIVE);

  private final Map<String, DataType> types =
      new HashMap<>() {
        {
          put("x", DataType.BIT);
          put("dbx", DataType.BIT);
          put("s", DataType.STRING);
          put("string", DataType.STRING);
          put("b", DataType.BYTE);
          put("dbb", DataType.BYTE);
          put("d", DataType.DOUBLE);
          put("int", DataType.INTEGER);
          put("dint", DataType.D_INTEGER);
          put("w", DataType.INTEGER);
          put("dbw", DataType.INTEGER);
          put("dul", DataType.U_LONG);
          put("dulint", DataType.U_LONG);
          put("dulong", DataType.U_LONG);
        }
      };

  public S7VariableAddress parse(String input) {
    Matcher match = regex.matcher(input);
    if (match.find()) {
      AreaType areaType = AreaType.valueOf(match.group("operand").toUpperCase());
      short dbNr = Short.parseShort(match.group("dbNr"));
      short start = Short.parseShort(match.group("start"));
      DataType type = parseType(match.group("type"));

      S7VariableAddress s7VariableAddress = new S7VariableAddress();
      s7VariableAddress.setAreaType(areaType);
      s7VariableAddress.setDbNr(dbNr);
      s7VariableAddress.setStart(start);
      s7VariableAddress.setType(type);

      if (type == DataType.BIT) {
        s7VariableAddress.setLength(1);
        s7VariableAddress.setBit(Byte.parseByte(match.group("bitOrLength")));
      } else if (type == DataType.BYTE) {
        s7VariableAddress.setLength(
            match.group("bitOrLength") != null ? Short.parseShort(match.group("bitOrLength")) : 1);
      } else if (type == DataType.STRING) {
        s7VariableAddress.setLength(
            match.group("bitOrLength") != null ? Short.parseShort(match.group("bitOrLength")) : 0);
      } else if (type == DataType.INTEGER) {
        s7VariableAddress.setLength(2);
      } else if (type == DataType.D_INTEGER) {
        s7VariableAddress.setLength(4);
      } else if (type == DataType.U_LONG) {
        s7VariableAddress.setLength(8);
      } else if (type == DataType.DOUBLE) {
        s7VariableAddress.setLength(4);
      }
      
      return s7VariableAddress;
    }

    return null;
  }

  private DataType parseType(String value) {
    for (Map.Entry<String, DataType> pair : types.entrySet()) {
      if (pair.getKey().equalsIgnoreCase(value)) {
        return pair.getValue();
      }
    }
    return null;
  }
}
