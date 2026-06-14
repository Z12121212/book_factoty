package com.z.bookcreat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.z.bookcreat.entity.LlmModel;
import com.z.bookcreat.entity.LlmProvider;
import com.z.bookcreat.entity.LlmScene;
import com.z.bookcreat.entity.LlmSceneModel;
import com.z.bookcreat.entity.NovelModelOverride;
import com.z.bookcreat.llm.ModelRouteResult;
import com.z.bookcreat.mapper.LlmModelMapper;
import com.z.bookcreat.mapper.LlmProviderMapper;
import com.z.bookcreat.mapper.LlmSceneMapper;
import com.z.bookcreat.mapper.LlmSceneModelMapper;
import com.z.bookcreat.mapper.NovelModelOverrideMapper;
import com.z.bookcreat.service.ModelRoutingService;
import com.z.bookcreat.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelRoutingServiceImpl implements ModelRoutingService {

    private final LlmSceneMapper llmSceneMapper;
    private final LlmSceneModelMapper llmSceneModelMapper;
    private final LlmModelMapper llmModelMapper;
    private final LlmProviderMapper llmProviderMapper;
    private final NovelModelOverrideMapper novelModelOverrideMapper;
    private final NovelService novelService;

    @Override
    public ModelRouteResult resolve(Long novelId, String sceneCode) {
        if (!StringUtils.hasText(sceneCode)) {
            throw new IllegalArgumentException("场景编码不能为空");
        }

        LlmScene scene = llmSceneMapper.selectOne(new LambdaQueryWrapper<LlmScene>()
                .eq(LlmScene::getSceneCode, sceneCode)
                .eq(LlmScene::getEnabled, 1)
                .last("LIMIT 1"));
        if (scene == null) {
            throw new IllegalArgumentException("模型场景不存在或已禁用");
        }

        if (novelId != null) {
            novelService.getByIdRequired(novelId);
            NovelModelOverride override = novelModelOverrideMapper.selectOne(new LambdaQueryWrapper<NovelModelOverride>()
                    .eq(NovelModelOverride::getNovelId, novelId)
                    .eq(NovelModelOverride::getSceneId, scene.getId())
                    .eq(NovelModelOverride::getEnabled, 1)
                    .last("LIMIT 1"));
            if (override != null) {
                return buildFromOverride(scene, override);
            }
        }

        List<LlmSceneModel> bindings = llmSceneModelMapper.selectList(new LambdaQueryWrapper<LlmSceneModel>()
                .eq(LlmSceneModel::getSceneId, scene.getId())
                .eq(LlmSceneModel::getEnabled, 1));
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("当前场景未绑定可用模型");
        }

        LlmSceneModel selected = bindings.stream()
                .filter(binding -> isModelAndProviderEnabled(binding.getModelId()))
                .sorted(Comparator.comparingInt(LlmSceneModel::getPriority)
                        .thenComparingInt(binding -> roleWeight(binding.getRoleType())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("当前场景没有可用模型"));

        return buildFromBinding(scene, selected);
    }

    private ModelRouteResult buildFromOverride(LlmScene scene, NovelModelOverride override) {
        LlmModel model = getEnabledModelRequired(override.getModelId());
        LlmProvider provider = getEnabledProviderRequired(model.getProviderId());
        return ModelRouteResult.builder()
                .providerId(provider.getId())
                .providerCode(provider.getProviderCode())
                .providerName(provider.getProviderName())
                .baseUrl(provider.getBaseUrl())
                .apiKey(provider.getApiKey())
                .modelId(model.getId())
                .modelCode(model.getModelCode())
                .modelName(model.getModelName())
                .modelType(model.getModelType())
                .sceneId(scene.getId())
                .sceneCode(scene.getSceneCode())
                .roleType("override")
                .priority(0)
                .temperature(override.getTemperature())
                .maxTokens(override.getMaxTokens())
                .topP(override.getTopP())
                .timeoutMs(override.getTimeoutMs())
                .sourceLevel("novel_override")
                .build();
    }

    private ModelRouteResult buildFromBinding(LlmScene scene, LlmSceneModel binding) {
        LlmModel model = getEnabledModelRequired(binding.getModelId());
        LlmProvider provider = getEnabledProviderRequired(model.getProviderId());
        return ModelRouteResult.builder()
                .providerId(provider.getId())
                .providerCode(provider.getProviderCode())
                .providerName(provider.getProviderName())
                .baseUrl(provider.getBaseUrl())
                .apiKey(provider.getApiKey())
                .modelId(model.getId())
                .modelCode(model.getModelCode())
                .modelName(model.getModelName())
                .modelType(model.getModelType())
                .sceneId(scene.getId())
                .sceneCode(scene.getSceneCode())
                .roleType(binding.getRoleType())
                .priority(binding.getPriority())
                .temperature(binding.getTemperature())
                .maxTokens(binding.getMaxTokens())
                .topP(binding.getTopP())
                .timeoutMs(binding.getTimeoutMs())
                .sourceLevel("scene_binding")
                .build();
    }

    private boolean isModelAndProviderEnabled(Long modelId) {
        LlmModel model = llmModelMapper.selectById(modelId);
        if (model == null || !Integer.valueOf(1).equals(model.getEnabled())) {
            return false;
        }
        LlmProvider provider = llmProviderMapper.selectById(model.getProviderId());
        return provider != null && Integer.valueOf(1).equals(provider.getEnabled());
    }

    private LlmModel getEnabledModelRequired(Long modelId) {
        LlmModel model = llmModelMapper.selectById(modelId);
        if (model == null || !Integer.valueOf(1).equals(model.getEnabled())) {
            throw new IllegalArgumentException("模型不存在或已禁用");
        }
        return model;
    }

    private LlmProvider getEnabledProviderRequired(Long providerId) {
        LlmProvider provider = llmProviderMapper.selectById(providerId);
        if (provider == null || !Integer.valueOf(1).equals(provider.getEnabled())) {
            throw new IllegalArgumentException("模型供应商不存在或已禁用");
        }
        return provider;
    }

    private int roleWeight(String roleType) {
        return switch (roleType) {
            case "primary" -> 0;
            case "cheap" -> 1;
            case "review" -> 2;
            case "fallback" -> 3;
            default -> 9;
        };
    }
}
