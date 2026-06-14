package com.z.bookcreat.service;

import com.z.bookcreat.dto.OutlineGenerateRequest;
import com.z.bookcreat.dto.OutlineGenerateResponse;

public interface OutlineGenerationService {

    OutlineGenerateResponse generate(OutlineGenerateRequest request);
}
