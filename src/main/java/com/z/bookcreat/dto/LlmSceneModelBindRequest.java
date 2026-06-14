package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LlmSceneModelBindRequest {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @NotBlank(message = "角色类型不能为空")
    private String roleType;

    private BigDecimal temperature;

    private Integer maxTokens;

    private BigDecimal topP;

    private Integer timeoutMs;
}
