package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LlmSceneCreateRequest {

    @NotBlank(message = "场景编码不能为空")
    @Size(max = 64, message = "场景编码不能超过64个字符")
    private String sceneCode;

    @NotBlank(message = "场景名称不能为空")
    @Size(max = 128, message = "场景名称不能超过128个字符")
    private String sceneName;

    @Size(max = 500, message = "场景描述不能超过500个字符")
    private String description;

    @NotBlank(message = "场景类型不能为空")
    private String sceneType;
}
