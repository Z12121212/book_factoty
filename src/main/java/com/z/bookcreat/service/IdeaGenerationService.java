package com.z.bookcreat.service;

import com.z.bookcreat.dto.IdeaGenerateRequest;
import com.z.bookcreat.dto.IdeaGenerateResponse;

public interface IdeaGenerationService {

    IdeaGenerateResponse generate(IdeaGenerateRequest request);
}
