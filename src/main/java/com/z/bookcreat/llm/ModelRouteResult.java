package com.z.bookcreat.llm;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ModelRouteResult {

    private Long providerId;

    private String providerCode;

    private String providerName;

    private String baseUrl;

    private String apiKey;

    private Long modelId;

    private String modelCode;

    private String modelName;

    private String modelType;

    private String metadataJson;

    private Long sceneId;

    private String sceneCode;

    private String roleType;

    private Integer priority;

    private BigDecimal temperature;

    private Integer maxTokens;

    private BigDecimal topP;

    private Integer timeoutMs;

    private String sourceLevel;
}
