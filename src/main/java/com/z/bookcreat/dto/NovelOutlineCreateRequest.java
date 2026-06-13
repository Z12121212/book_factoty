package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NovelOutlineCreateRequest {

    @NotNull(message = "小说ID不能为空")
    private Long novelId;

    @NotBlank(message = "大纲类型不能为空")
    private String outlineType;

    @Size(max = 128, message = "大纲标题不能超过128个字符")
    private String title;

    @NotBlank(message = "大纲内容不能为空")
    private String content;
}
