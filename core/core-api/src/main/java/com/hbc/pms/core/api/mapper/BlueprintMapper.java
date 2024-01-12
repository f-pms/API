package com.hbc.pms.core.api.mapper;

import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMapper {
  BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

  Blueprint toBlueprint(BlueprintEntity source);

  BlueprintEntity toBlueprintEntity(Blueprint source);
}
