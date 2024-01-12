package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.Blueprint;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("blueprints")
@RequiredArgsConstructor
public class BlueprintController {
    private final BlueprintService blueprintService;

    @GetMapping()
    public ApiResponse<List<Blueprint>> getBlueprints() {
        var response = blueprintService.getAll();
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<Blueprint> getById(@PathVariable Long id) {
        var response = blueprintService.getById(id);
        return ApiResponse.success(response);
    }
}
