package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LlmProviderCreateRequest {

    @NotBlank(message = "供应商编码不能为空")
    @Size(max = 64, message = "供应商编码不能超过64个字符")
    private String providerCode;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 128, message = "供应商名称不能超过128个字符")
    private String providerName;

    @NotBlank(message = "Base URL不能为空")
    @Size(max = 512, message = "Base URL不能超过512个字符")
    private String baseUrl;

    @NotBlank(message = "API Key不能为空")
    @Size(max = 512, message = "API Key不能超过512个字符")
    private String apiKey;
}
