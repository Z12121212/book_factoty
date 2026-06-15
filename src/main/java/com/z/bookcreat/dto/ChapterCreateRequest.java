package com.z.bookcreat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChapterCreateRequest {

    @NotNull(message = "小说ID不能为空")
    private Long novelId;

    private Long volumeId;

    @NotNull(message = "章节号不能为空")
    private Integer chapterNo;

    @Size(max = 128, message = "章节标题不能超过128个字符")
    private String title;

    @NotBlank(message = "章节大纲不能为空")
    private String outline;
}
