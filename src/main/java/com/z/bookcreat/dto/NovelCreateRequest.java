package com.z.bookcreat.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NovelCreateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "题材ID不能为空")
    private Long genreId;

    @NotBlank(message = "小说标题不能为空")
    @Size(max = 128, message = "小说标题不能超过128个字符")
    private String title;

    @NotBlank(message = "核心创意不能为空")
    private String idea;

    @Min(value = 1, message = "目标总字数必须大于0")
    private Integer targetWordCount;

    @Min(value = 500, message = "每章字数不能小于500")
    private Integer wordsPerChapter;

    private String writingStyle;

    private String configJson;
}
