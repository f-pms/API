package com.hbc.pms.core.api.controller.v1.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlueprintRequest {

  private String name;
  private String description;
}
