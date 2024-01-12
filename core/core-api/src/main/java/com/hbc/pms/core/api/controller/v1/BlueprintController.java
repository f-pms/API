package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.Blueprint;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("blueprints")
@RequiredArgsConstructor
public class BlueprintController {
    private final BlueprintService blueprintService;

    @GetMapping()
    public ApiResponse<List<Blueprint>> getBlueprints() {
        return ApiResponse.success(blueprintService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Blueprint> getById(@PathVariable Long id) {
        return ApiResponse.success(blueprintService.getById(id));
    }
}
