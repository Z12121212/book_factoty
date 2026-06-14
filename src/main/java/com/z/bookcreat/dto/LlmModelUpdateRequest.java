package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LlmModelUpdateRequest {

    @NotNull(message = "模型ID不能为空")
    private Long id;

    @NotBlank(message = "模型名称不能为空")
    @Size(max = 128, message = "模型名称不能超过128个字符")
    private String modelName;

    @NotBlank(message = "模型类型不能为空")
    private String modelType;

    private Integer contextWindow;

    private Integer maxOutputTokens;

    private Integer supportsStream;

    private Integer supportsJson;

    private Integer supportsTools;

    @NotNull(message = "启用状态不能为空")
    private Integer enabled;

    private String metadataJson;
}
