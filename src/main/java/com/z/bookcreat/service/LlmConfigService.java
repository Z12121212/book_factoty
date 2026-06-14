package com.z.bookcreat.service;

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

import java.util.List;

public interface LlmConfigService {

    Long createProvider(LlmProviderCreateRequest request);

    void updateProvider(LlmProviderUpdateRequest request);

    List<LlmProvider> listProviders();

    Long createModel(LlmModelCreateRequest request);

    void updateModel(LlmModelUpdateRequest request);

    List<LlmModel> listModels(Long providerId, String modelType);

    Long createScene(LlmSceneCreateRequest request);

    void updateScene(LlmSceneUpdateRequest request);

    List<LlmScene> listScenes();

    Long bindSceneModel(LlmSceneModelBindRequest request);

    List<LlmSceneModel> listSceneModels(Long sceneId);

    Long saveNovelOverride(NovelModelOverrideRequest request);

    List<NovelModelOverride> listNovelOverrides(Long novelId);
}
