package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NovelModelOverrideRequest {

    @NotNull(message = "小说ID不能为空")
    private Long novelId;

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    private BigDecimal temperature;

    private Integer maxTokens;

    private BigDecimal topP;

    private Integer timeoutMs;

    @NotNull(message = "启用状态不能为空")
    private Integer enabled;
}
