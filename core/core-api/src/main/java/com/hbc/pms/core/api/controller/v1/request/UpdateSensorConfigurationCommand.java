package com.hbc.pms.core.api.controller.v1.request;

import java.text.MessageFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateSensorConfigurationCommand {

  private String address;
  private Integer db;
  private Integer offset;
  private String dataType;

  public void aggregatePlcAddress() {
    if (address == null) {
      address = MessageFormat.format("%DB{0,number,#}:{1,number,#}:{2}", db, offset, dataType);
    }
  }
}
