package com.z.bookcreat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.z.bookcreat.mapper.LlmModelMapper;
import com.z.bookcreat.mapper.LlmProviderMapper;
import com.z.bookcreat.mapper.LlmSceneMapper;
import com.z.bookcreat.mapper.LlmSceneModelMapper;
import com.z.bookcreat.mapper.NovelModelOverrideMapper;
import com.z.bookcreat.service.LlmConfigService;
import com.z.bookcreat.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LlmConfigServiceImpl implements LlmConfigService {

    private static final Set<String> MODEL_TYPES = Set.of("chat", "embedding");
    private static final Set<String> SCENE_TYPES = Set.of("generate", "extract", "review", "embed");
    private static final Set<String> ROLE_TYPES = Set.of("primary", "fallback", "cheap", "review");

    private final LlmProviderMapper llmProviderMapper;
    private final LlmModelMapper llmModelMapper;
    private final LlmSceneMapper llmSceneMapper;
    private final LlmSceneModelMapper llmSceneModelMapper;
    private final NovelModelOverrideMapper novelModelOverrideMapper;
    private final NovelService novelService;

    @Override
    @Transactional
    public Long createProvider(LlmProviderCreateRequest request) {
        ensureProviderCodeUnique(request.getProviderCode(), null);

        LocalDateTime now = LocalDateTime.now();
        LlmProvider provider = new LlmProvider();
        provider.setProviderCode(request.getProviderCode());
        provider.setProviderName(request.getProviderName());
        provider.setBaseUrl(request.getBaseUrl());
        provider.setApiKey(request.getApiKey());
        provider.setEnabled(1);
        provider.setCreatedAt(now);
        provider.setUpdatedAt(now);
        llmProviderMapper.insert(provider);
        return provider.getId();
    }

    @Override
    @Transactional
    public void updateProvider(LlmProviderUpdateRequest request) {
        LlmProvider existing = getProviderRequired(request.getId());
        existing.setProviderName(request.getProviderName());
        existing.setBaseUrl(request.getBaseUrl());
        existing.setApiKey(request.getApiKey());
        existing.setEnabled(request.getEnabled());
        existing.setUpdatedAt(LocalDateTime.now());
        llmProviderMapper.updateById(existing);
    }

    @Override
    public List<LlmProvider> listProviders() {
        return llmProviderMapper.selectList(new LambdaQueryWrapper<LlmProvider>()
                .orderByAsc(LlmProvider::getProviderCode));
    }

    @Override
    @Transactional
    public Long createModel(LlmModelCreateRequest request) {
        validateModelType(request.getModelType());
        getProviderRequired(request.getProviderId());
        ensureModelCodeUnique(request.getProviderId(), request.getModelCode(), null);

        LocalDateTime now = LocalDateTime.now();
        LlmModel model = new LlmModel();
        model.setProviderId(request.getProviderId());
        model.setModelCode(request.getModelCode());
        model.setModelName(request.getModelName());
        model.setModelType(request.getModelType());
        model.setContextWindow(request.getContextWindow());
        model.setMaxOutputTokens(request.getMaxOutputTokens());
        model.setSupportsStream(defaultFlag(request.getSupportsStream()));
        model.setSupportsJson(defaultFlag(request.getSupportsJson()));
        model.setSupportsTools(defaultFlag(request.getSupportsTools()));
        model.setEnabled(1);
        model.setMetadataJson(request.getMetadataJson());
        model.setCreatedAt(now);
        model.setUpdatedAt(now);
        llmModelMapper.insert(model);
        return model.getId();
    }

    @Override
    @Transactional
    public void updateModel(LlmModelUpdateRequest request) {
        validateModelType(request.getModelType());
        LlmModel model = getModelRequired(request.getId());
        model.setModelName(request.getModelName());
        model.setModelType(request.getModelType());
        model.setContextWindow(request.getContextWindow());
        model.setMaxOutputTokens(request.getMaxOutputTokens());
        model.setSupportsStream(defaultFlag(request.getSupportsStream()));
        model.setSupportsJson(defaultFlag(request.getSupportsJson()));
        model.setSupportsTools(defaultFlag(request.getSupportsTools()));
        model.setEnabled(request.getEnabled());
        model.setMetadataJson(request.getMetadataJson());
        model.setUpdatedAt(LocalDateTime.now());
        llmModelMapper.updateById(model);
    }

    @Override
    public List<LlmModel> listModels(Long providerId, String modelType) {
        return llmModelMapper.selectList(new LambdaQueryWrapper<LlmModel>()
                .eq(providerId != null, LlmModel::getProviderId, providerId)
                .eq(StringUtils.hasText(modelType), LlmModel::getModelType, modelType)
                .orderByAsc(LlmModel::getProviderId)
                .orderByAsc(LlmModel::getModelCode));
    }

    @Override
    @Transactional
    public Long createScene(LlmSceneCreateRequest request) {
        validateSceneType(request.getSceneType());
        ensureSceneCodeUnique(request.getSceneCode(), null);

        LocalDateTime now = LocalDateTime.now();
        LlmScene scene = new LlmScene();
        scene.setSceneCode(request.getSceneCode());
        scene.setSceneName(request.getSceneName());
        scene.setDescription(request.getDescription());
        scene.setSceneType(request.getSceneType());
        scene.setEnabled(1);
        scene.setCreatedAt(now);
        scene.setUpdatedAt(now);
        llmSceneMapper.insert(scene);
        return scene.getId();
    }

    @Override
    @Transactional
    public void updateScene(LlmSceneUpdateRequest request) {
        validateSceneType(request.getSceneType());
        LlmScene scene = getSceneRequired(request.getId());
        scene.setSceneName(request.getSceneName());
        scene.setDescription(request.getDescription());
        scene.setSceneType(request.getSceneType());
        scene.setEnabled(request.getEnabled());
        scene.setUpdatedAt(LocalDateTime.now());
        llmSceneMapper.updateById(scene);
    }

    @Override
    public List<LlmScene> listScenes() {
        return llmSceneMapper.selectList(new LambdaQueryWrapper<LlmScene>()
                .orderByAsc(LlmScene::getSceneCode));
    }

    @Override
    @Transactional
    public Long bindSceneModel(LlmSceneModelBindRequest request) {
        validateRoleType(request.getRoleType());
        getSceneRequired(request.getSceneId());
        getModelRequired(request.getModelId());

        LlmSceneModel existing = llmSceneModelMapper.selectOne(new LambdaQueryWrapper<LlmSceneModel>()
                .eq(LlmSceneModel::getSceneId, request.getSceneId())
                .eq(LlmSceneModel::getModelId, request.getModelId())
                .eq(LlmSceneModel::getRoleType, request.getRoleType())
                .last("LIMIT 1"));

        LocalDateTime now = LocalDateTime.now();
        if (existing != null) {
            existing.setPriority(request.getPriority());
            existing.setTemperature(request.getTemperature());
            existing.setMaxTokens(request.getMaxTokens());
            existing.setTopP(request.getTopP());
            existing.setTimeoutMs(request.getTimeoutMs());
            existing.setEnabled(1);
            existing.setUpdatedAt(now);
            llmSceneModelMapper.updateById(existing);
            return existing.getId();
        }

        LlmSceneModel binding = new LlmSceneModel();
        binding.setSceneId(request.getSceneId());
        binding.setModelId(request.getModelId());
        binding.setPriority(request.getPriority());
        binding.setRoleType(request.getRoleType());
        binding.setTemperature(request.getTemperature());
        binding.setMaxTokens(request.getMaxTokens());
        binding.setTopP(request.getTopP());
        binding.setTimeoutMs(request.getTimeoutMs());
        binding.setEnabled(1);
        binding.setCreatedAt(now);
        binding.setUpdatedAt(now);
        llmSceneModelMapper.insert(binding);
        return binding.getId();
    }

    @Override
    public List<LlmSceneModel> listSceneModels(Long sceneId) {
        getSceneRequired(sceneId);
        return llmSceneModelMapper.selectList(new LambdaQueryWrapper<LlmSceneModel>()
                .eq(LlmSceneModel::getSceneId, sceneId)
                .orderByAsc(LlmSceneModel::getPriority)
                .orderByAsc(LlmSceneModel::getRoleType));
    }

    @Override
    @Transactional
    public Long saveNovelOverride(NovelModelOverrideRequest request) {
        novelService.getByIdRequired(request.getNovelId());
        getSceneRequired(request.getSceneId());
        getModelRequired(request.getModelId());

        NovelModelOverride existing = novelModelOverrideMapper.selectOne(new LambdaQueryWrapper<NovelModelOverride>()
                .eq(NovelModelOverride::getNovelId, request.getNovelId())
                .eq(NovelModelOverride::getSceneId, request.getSceneId())
                .last("LIMIT 1"));

        LocalDateTime now = LocalDateTime.now();
        if (existing != null) {
            existing.setModelId(request.getModelId());
            existing.setTemperature(request.getTemperature());
            existing.setMaxTokens(request.getMaxTokens());
            existing.setTopP(request.getTopP());
            existing.setTimeoutMs(request.getTimeoutMs());
            existing.setEnabled(request.getEnabled());
            existing.setUpdatedAt(now);
            novelModelOverrideMapper.updateById(existing);
            return existing.getId();
        }

        NovelModelOverride override = new NovelModelOverride();
        override.setNovelId(request.getNovelId());
        override.setSceneId(request.getSceneId());
        override.setModelId(request.getModelId());
        override.setTemperature(request.getTemperature());
        override.setMaxTokens(request.getMaxTokens());
        override.setTopP(request.getTopP());
        override.setTimeoutMs(request.getTimeoutMs());
        override.setEnabled(request.getEnabled());
        override.setCreatedAt(now);
        override.setUpdatedAt(now);
        novelModelOverrideMapper.insert(override);
        return override.getId();
    }

    @Override
    public List<NovelModelOverride> listNovelOverrides(Long novelId) {
        novelService.getByIdRequired(novelId);
        return novelModelOverrideMapper.selectList(new LambdaQueryWrapper<NovelModelOverride>()
                .eq(NovelModelOverride::getNovelId, novelId)
                .orderByAsc(NovelModelOverride::getSceneId));
    }

    private LlmProvider getProviderRequired(Long id) {
        LlmProvider provider = llmProviderMapper.selectById(id);
        if (provider == null) {
            throw new IllegalArgumentException("模型供应商不存在");
        }
        return provider;
    }

    private LlmModel getModelRequired(Long id) {
        LlmModel model = llmModelMapper.selectById(id);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在");
        }
        return model;
    }

    private LlmScene getSceneRequired(Long id) {
        LlmScene scene = llmSceneMapper.selectById(id);
        if (scene == null) {
            throw new IllegalArgumentException("模型场景不存在");
        }
        return scene;
    }

    private void ensureProviderCodeUnique(String providerCode, Long excludeId) {
        Long count = llmProviderMapper.selectCount(new LambdaQueryWrapper<LlmProvider>()
                .eq(LlmProvider::getProviderCode, providerCode)
                .ne(excludeId != null, LlmProvider::getId, excludeId));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("供应商编码已存在");
        }
    }

    private void ensureModelCodeUnique(Long providerId, String modelCode, Long excludeId) {
        Long count = llmModelMapper.selectCount(new LambdaQueryWrapper<LlmModel>()
                .eq(LlmModel::getProviderId, providerId)
                .eq(LlmModel::getModelCode, modelCode)
                .ne(excludeId != null, LlmModel::getId, excludeId));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("同一供应商下模型编码已存在");
        }
    }

    private void ensureSceneCodeUnique(String sceneCode, Long excludeId) {
        Long count = llmSceneMapper.selectCount(new LambdaQueryWrapper<LlmScene>()
                .eq(LlmScene::getSceneCode, sceneCode)
                .ne(excludeId != null, LlmScene::getId, excludeId));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("场景编码已存在");
        }
    }

    private void validateModelType(String modelType) {
        if (!MODEL_TYPES.contains(modelType)) {
            throw new IllegalArgumentException("模型类型不合法");
        }
    }

    private void validateSceneType(String sceneType) {
        if (!SCENE_TYPES.contains(sceneType)) {
            throw new IllegalArgumentException("场景类型不合法");
        }
    }

    private void validateRoleType(String roleType) {
        if (!ROLE_TYPES.contains(roleType)) {
            throw new IllegalArgumentException("场景模型角色不合法");
        }
    }

    private int defaultFlag(Integer value) {
        return value == null ? 0 : value;
    }
}
