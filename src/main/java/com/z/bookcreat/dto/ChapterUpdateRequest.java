package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChapterUpdateRequest {

    @NotNull(message = "章节ID不能为空")
    private Long id;

    @Size(max = 128, message = "章节标题不能超过128个字符")
    private String title;

    @NotBlank(message = "章节大纲不能为空")
    private String outline;
}
