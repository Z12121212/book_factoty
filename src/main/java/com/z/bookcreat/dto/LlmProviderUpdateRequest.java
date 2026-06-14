package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LlmProviderUpdateRequest {

    @NotNull(message = "供应商ID不能为空")
    private Long id;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 128, message = "供应商名称不能超过128个字符")
    private String providerName;

    @NotBlank(message = "Base URL不能为空")
    @Size(max = 512, message = "Base URL不能超过512个字符")
    private String baseUrl;

    @NotBlank(message = "API Key不能为空")
    @Size(max = 512, message = "API Key不能超过512个字符")
    private String apiKey;

    @NotNull(message = "启用状态不能为空")
    private Integer enabled;
}
