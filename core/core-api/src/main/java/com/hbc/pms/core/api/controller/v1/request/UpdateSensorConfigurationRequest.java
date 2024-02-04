package com.hbc.pms.core.api.controller.v1.request;

import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

@Getter
@Setter
public class UpdateSensorConfigurationRequest {
  private String address;
  private Integer db;
  private Integer offset;
  private String dataType;

  public void aggregateData() {
    if (address == null) {
      address = MessageFormat.format("%DB{0,number,#}:{1,number,#}:{2}", db, offset, dataType);
    }
  }
}
