package com.z.bookcreat.service;

import com.z.bookcreat.dto.ChapterGenerateRequest;
import com.z.bookcreat.dto.ChapterGenerateResponse;

public interface ChapterGenerationService {

    ChapterGenerateResponse generate(ChapterGenerateRequest request);
}
