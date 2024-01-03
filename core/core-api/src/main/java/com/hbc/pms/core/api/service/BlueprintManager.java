package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.io.Blueprint;
import com.hbc.pms.plc.io.IoBlueprintService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Getter
public class BlueprintManager {
    private List<Blueprint> blueprints;

    private final IoBlueprintService ioBlueprintService;

    public BlueprintManager(IoBlueprintService ioBlueprintService) {
        this.ioBlueprintService = ioBlueprintService;
    }

    @PostConstruct
    public void loadBlueprints() {
        blueprints = ioBlueprintService.getAll();
    }
}
