package com.hbc.pms.plc.integration.plc4x;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.exceptions.NotSupportedPlcResponseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.value.PlcValue;
import org.apache.plc4x.java.spi.messages.DefaultPlcReadResponse;

@UtilityClass
@Slf4j
public class PlcUtil {

  public static Map<String, IoResponse> convertPlcResponseToMap(PlcReadResponse plcReadResponse)
      throws NotSupportedPlcResponseException {
    Map<String, IoResponse> result = new HashMap<>();
    if (plcReadResponse instanceof DefaultPlcReadResponse defaultPlcReadResponse) {
      for (var entry : defaultPlcReadResponse.getValues().keySet()) {
        IoResponse ioResponse = getIoResponse(defaultPlcReadResponse, entry);
        result.put(entry, ioResponse);
      }
    } else {
      throw new NotSupportedPlcResponseException(
          "PlcReadResponse is not instance of DefaultPlcReadResponse, throwing an exception");
    }
    return result;
  }

  public static IoResponse getIoResponse(PlcReadResponse defaultPlcReadResponse, String entry) {
    IoResponse ioResponse = new IoResponse();
    PlcValue plcValue = defaultPlcReadResponse.getPlcValue(entry);
    ioResponse.setPlcValue(plcValue);
    ioResponse.setVariableName(entry);
    ioResponse.setLocalDateTime(LocalDateTime.now());
    return ioResponse;
  }
}
