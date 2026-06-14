package com.z.bookcreat.dto;

import com.z.bookcreat.llm.ModelRouteResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdeaGenerateResponse {

    private List<IdeaOption> ideas;

    private ModelRouteResult route;

    private String rawContent;
}
