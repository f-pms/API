package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.BlueprintType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchSensorConfigurationCommand {
  private BlueprintType blueprintType;
  private String blueprintName;
}
