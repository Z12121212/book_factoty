package com.z.bookcreat.dto;

import com.z.bookcreat.llm.ModelRouteResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OutlineGenerateResponse {

    private String title;

    private String content;

    private ModelRouteResult route;

    private String rawContent;
}
