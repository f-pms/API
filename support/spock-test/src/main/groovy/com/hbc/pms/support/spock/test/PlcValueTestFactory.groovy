package com.hbc.pms.support.spock.test

import lombok.Getter
import lombok.Setter
import org.apache.plc4x.java.api.types.PlcResponseCode
import org.apache.plc4x.java.api.types.PlcValueType
import org.apache.plc4x.java.api.value.PlcValue
import org.apache.plc4x.java.s7.readwrite.tag.S7Tag
import org.apache.plc4x.java.spi.messages.utils.ResponseItem
import org.apache.plc4x.java.spi.values.PlcBOOL
import org.apache.plc4x.java.spi.values.PlcIECValue
import org.apache.plc4x.java.spi.values.PlcINT
import org.apache.plc4x.java.spi.values.PlcREAL

@Getter
@Setter
class PlcValueTestFactory {
  Map<String, PlcIECValue> iecValueMap = new HashMap<>()

  void setCurrentValue(String address, Object currentValue) {
    if (!iecValueMap.containsKey(address)) {
      initBasedOnType(address)
    }
    this.iecValueMap.get(address).value = currentValue
  }

  ResponseItem<PlcValue> respondItem(String address) {
    if (!iecValueMap.containsKey(address)) {
      initBasedOnType(address)
    }
    return new ResponseItem<>(PlcResponseCode.OK, iecValueMap.get(address))
  }

  private void initBasedOnType(String address) {
    def tag = S7Tag.of(address)
    switch (tag.getPlcValueType()) {
      case PlcValueType.BOOL:
        iecValueMap.put(address, new PlcBOOL(false))
        break;
      case PlcValueType.REAL:
        iecValueMap.put(address, new PlcREAL(0f))
        break;
      default:
        iecValueMap.put(address, new PlcINT(0))
    }
  }
}
