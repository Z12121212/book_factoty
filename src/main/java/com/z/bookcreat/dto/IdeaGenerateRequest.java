package com.z.bookcreat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdeaGenerateRequest {

    private Long novelId;

    @NotBlank(message = "题材不能为空")
    private String genreName;

    private String userIdea;

    @Min(value = 1, message = "创意数量不能小于1")
    @Max(value = 5, message = "创意数量不能大于5")
    private Integer count = 3;

    private String sceneCode = "idea_generate";
}
