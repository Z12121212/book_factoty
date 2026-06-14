package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LlmSceneUpdateRequest {

    @NotNull(message = "场景ID不能为空")
    private Long id;

    @NotBlank(message = "场景名称不能为空")
    @Size(max = 128, message = "场景名称不能超过128个字符")
    private String sceneName;

    @Size(max = 500, message = "场景描述不能超过500个字符")
    private String description;

    @NotBlank(message = "场景类型不能为空")
    private String sceneType;

    @NotNull(message = "启用状态不能为空")
    private Integer enabled;
}
