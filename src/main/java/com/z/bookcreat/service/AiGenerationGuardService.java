package com.z.bookcreat.service;

public interface AiGenerationGuardService {

    void acquire(String sceneCode);

    void release(String sceneCode);
}
