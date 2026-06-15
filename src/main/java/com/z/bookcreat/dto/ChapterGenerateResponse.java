package com.z.bookcreat.dto;

import com.z.bookcreat.llm.ModelRouteResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterGenerateResponse {

    private Long chapterId;

    private String title;

    private String content;

    private Integer wordCount;

    private ModelRouteResult route;

    private String rawContent;
}
