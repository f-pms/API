package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.repository.BlueprintRepository;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlueprintPersistenceService {
    private final ModelMapper mapper;
    private final BlueprintRepository blueprintRepository;

    public List<Blueprint> getAll() {
        return StreamSupport
            .stream(blueprintRepository.findAll().spliterator(), false)
            .map(b -> mapper.map(b, Blueprint.class))
            .toList();
    }

  public List<String> getAllAddresses() {
    return getAll().stream().flatMap(blueprint -> blueprint.getAddresses().stream()).toList();
  }

    public Blueprint getById(Long id) {
        var oBlueprint = blueprintRepository.findById(id);
        if (oBlueprint.isEmpty()) {
            throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Blueprint not found with id: " + id);
        }
        return mapper.map(oBlueprint.get(), Blueprint.class);
    }

    public Blueprint create(Blueprint blueprint) {
        var entity = mapper.map(blueprint, BlueprintEntity.class);
        return mapper.map(blueprintRepository.save(entity), Blueprint.class);
    }

    public Blueprint update(Blueprint blueprint) {
        var existedBlueprint = getById(blueprint.getId());
        mapper.map(blueprint, existedBlueprint);
        return mapper.map(
            blueprintRepository.save(mapper.map(existedBlueprint, BlueprintEntity.class)),
            Blueprint.class
        );
    }

}
