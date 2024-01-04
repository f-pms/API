package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.plc.io.Blueprint;
import com.hbc.pms.plc.io.IoBlueprintService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlueprintService {
    private final IoBlueprintService ioBlueprintService;

    public BlueprintService(IoBlueprintService ioBlueprintService) {
        this.ioBlueprintService = ioBlueprintService;
    }

    public List<Blueprint> getAll() {
        return ioBlueprintService.getAll();
    }

    public Blueprint getById(String id) {
        var blueprint = ioBlueprintService.getById(id);
        if (blueprint == null) {
            throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Blueprint not found with id: " + id);
        }
        return blueprint;
    }
}
