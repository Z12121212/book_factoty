package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NovelOutlineUpdateRequest {

    @NotNull(message = "大纲ID不能为空")
    private Long id;

    @Size(max = 128, message = "大纲标题不能超过128个字符")
    private String title;

    @NotBlank(message = "大纲内容不能为空")
    private String content;
}
