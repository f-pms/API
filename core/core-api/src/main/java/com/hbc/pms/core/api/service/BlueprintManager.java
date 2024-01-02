package com.hbc.pms.core.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.api.domain.Blueprint;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class BlueprintManager {
    private List<Blueprint> blueprints;

    @PostConstruct
    public void loadBlueprints() throws IOException {
        blueprints = new ArrayList<>();
        String BLUEPRINTS_DIR = "\\core\\core-api\\src\\main\\resources\\blueprints";
        String blueprintsPath = new FileSystemResource("").getFile().getAbsolutePath() + BLUEPRINTS_DIR;
        File[] blueprintsDir = new File(blueprintsPath).listFiles();

        if (blueprintsDir == null) {
            return;
        }

        for (File blueprintFile : blueprintsDir) {
            ObjectMapper objectMapper = new ObjectMapper();
            Blueprint blueprint = objectMapper.readValue(blueprintFile, Blueprint.class);
            blueprints.add(blueprint);
        }
    }
}
