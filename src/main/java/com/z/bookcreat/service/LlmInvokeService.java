package com.z.bookcreat.service;

import com.z.bookcreat.llm.LlmChatRequest;
import com.z.bookcreat.llm.LlmChatResponse;

public interface LlmInvokeService {

    LlmChatResponse chat(LlmChatRequest request);
}
