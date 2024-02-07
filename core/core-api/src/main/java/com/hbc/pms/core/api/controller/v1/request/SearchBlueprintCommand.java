package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.BlueprintType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchBlueprintCommand {
  private BlueprintType blueprintType;
  private String blueprintName;
}
