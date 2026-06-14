package com.z.bookcreat.controller;

import com.z.bookcreat.common.ApiResponse;
import com.z.bookcreat.dto.LlmModelCreateRequest;
import com.z.bookcreat.dto.LlmModelUpdateRequest;
import com.z.bookcreat.dto.LlmProviderCreateRequest;
import com.z.bookcreat.dto.LlmProviderUpdateRequest;
import com.z.bookcreat.dto.LlmSceneCreateRequest;
import com.z.bookcreat.dto.LlmSceneModelBindRequest;
import com.z.bookcreat.dto.LlmSceneUpdateRequest;
import com.z.bookcreat.dto.NovelModelOverrideRequest;
import com.z.bookcreat.entity.LlmModel;
import com.z.bookcreat.entity.LlmProvider;
import com.z.bookcreat.entity.LlmScene;
import com.z.bookcreat.entity.LlmSceneModel;
import com.z.bookcreat.entity.NovelModelOverride;
import com.z.bookcreat.llm.ModelRouteResult;
import com.z.bookcreat.service.LlmConfigService;
import com.z.bookcreat.service.ModelRoutingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/llm")
public class LlmConfigController {

    private final LlmConfigService llmConfigService;
    private final ModelRoutingService modelRoutingService;

    @PostMapping("/providers")
    public ApiResponse<Long> createProvider(@Valid @RequestBody LlmProviderCreateRequest request) {
        return ApiResponse.ok(llmConfigService.createProvider(request));
    }

    @PutMapping("/providers/{id}")
    public ApiResponse<Void> updateProvider(@PathVariable Long id, @Valid @RequestBody LlmProviderUpdateRequest request) {
        request.setId(id);
        llmConfigService.updateProvider(request);
        return ApiResponse.ok();
    }

    @GetMapping("/providers")
    public ApiResponse<List<LlmProvider>> listProviders() {
        return ApiResponse.ok(llmConfigService.listProviders());
    }

    @PostMapping("/models")
    public ApiResponse<Long> createModel(@Valid @RequestBody LlmModelCreateRequest request) {
        return ApiResponse.ok(llmConfigService.createModel(request));
    }

    @PutMapping("/models/{id}")
    public ApiResponse<Void> updateModel(@PathVariable Long id, @Valid @RequestBody LlmModelUpdateRequest request) {
        request.setId(id);
        llmConfigService.updateModel(request);
        return ApiResponse.ok();
    }

    @GetMapping("/models")
    public ApiResponse<List<LlmModel>> listModels(@RequestParam(required = false) Long providerId,
                                                  @RequestParam(required = false) String modelType) {
        return ApiResponse.ok(llmConfigService.listModels(providerId, modelType));
    }

    @PostMapping("/scenes")
    public ApiResponse<Long> createScene(@Valid @RequestBody LlmSceneCreateRequest request) {
        return ApiResponse.ok(llmConfigService.createScene(request));
    }

    @PutMapping("/scenes/{id}")
    public ApiResponse<Void> updateScene(@PathVariable Long id, @Valid @RequestBody LlmSceneUpdateRequest request) {
        request.setId(id);
        llmConfigService.updateScene(request);
        return ApiResponse.ok();
    }

    @GetMapping("/scenes")
    public ApiResponse<List<LlmScene>> listScenes() {
        return ApiResponse.ok(llmConfigService.listScenes());
    }

    @PostMapping("/scene-models")
    public ApiResponse<Long> bindSceneModel(@Valid @RequestBody LlmSceneModelBindRequest request) {
        return ApiResponse.ok(llmConfigService.bindSceneModel(request));
    }

    @GetMapping("/scene-models")
    public ApiResponse<List<LlmSceneModel>> listSceneModels(@RequestParam Long sceneId) {
        return ApiResponse.ok(llmConfigService.listSceneModels(sceneId));
    }

    @PostMapping("/novel-overrides")
    public ApiResponse<Long> saveNovelOverride(@Valid @RequestBody NovelModelOverrideRequest request) {
        return ApiResponse.ok(llmConfigService.saveNovelOverride(request));
    }

    @GetMapping("/novel-overrides")
    public ApiResponse<List<NovelModelOverride>> listNovelOverrides(@RequestParam Long novelId) {
        return ApiResponse.ok(llmConfigService.listNovelOverrides(novelId));
    }

    @GetMapping("/route")
    public ApiResponse<ModelRouteResult> resolve(@RequestParam(required = false) Long novelId,
                                                 @RequestParam String sceneCode) {
        return ApiResponse.ok(modelRoutingService.resolve(novelId, sceneCode));
    }
}
