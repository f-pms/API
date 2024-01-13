package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import com.hbc.pms.integration.db.repository.BlueprintRepository;
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class BlueprintService {
    private final ModelMapper mapper;
    private final BlueprintRepository blueprintRepository;
    private final SensorConfigurationRepository sensorConfigurationRepository;

    public List<Blueprint> getAll() {
        return StreamSupport
            .stream(blueprintRepository.findAll().spliterator(), false)
            .map(b -> mapper.map(b, Blueprint.class))
            .toList();
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
