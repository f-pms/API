package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.mapper.BlueprintMapper;
import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.integration.db.repository.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class BlueprintService {
    private final BlueprintRepository blueprintRepository;

    public List<Blueprint> getAll() {
        return StreamSupport
            .stream(blueprintRepository.findAll().spliterator(), false)
            .map(BlueprintMapper.INSTANCE::toBlueprint)
            .toList();
    }

    public Blueprint getById(Long id) {
        var blueprint = blueprintRepository.findById(id);
        if (blueprint.isEmpty()) {
            throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Blueprint not found with id: " + id);
        }
        return BlueprintMapper.INSTANCE.toBlueprint(blueprint.get());
    }

    public void createBlueprint(Blueprint blueprint) {
        var entity = BlueprintMapper.INSTANCE.toBlueprintEntity(blueprint);
        blueprintRepository.save(entity);
    }
}
