package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LlmModelCreateRequest {

    @NotNull(message = "供应商ID不能为空")
    private Long providerId;

    @NotBlank(message = "模型编码不能为空")
    @Size(max = 128, message = "模型编码不能超过128个字符")
    private String modelCode;

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

    private String metadataJson;
}
