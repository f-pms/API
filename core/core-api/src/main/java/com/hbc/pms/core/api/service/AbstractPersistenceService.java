package com.hbc.pms.core.api.service;

import java.util.Collection;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public abstract class AbstractPersistenceService<V> {
  @Autowired protected ModelMapper mapper;

  public <K> K mapToModel(V entity, Class<K> clazzModel) {
    return mapper.map(entity, clazzModel);
  }

  public <K> List<K> mapToModel(Collection<V> entities, Class<K> clazzModel) {
    return entities.stream().map(entity -> mapToModel(entity, clazzModel)).toList();
  }

  public <K> Page<K> mapToModel(Page<V> entities, Class<K> clazzModel) {
    return entities.map(entity -> mapToModel(entity, clazzModel));
  }

  public <K> V mapToEntity(K model, Class<V> clazzEntity) {
    return mapper.map(model, clazzEntity);
  }

  public <K> List<V> mapToEntity(Collection<K> models, Class<V> clazzEntity) {
    return models.stream().map(model -> mapToEntity(model, clazzEntity)).toList();
  }
}
