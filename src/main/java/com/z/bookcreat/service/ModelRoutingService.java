package com.z.bookcreat.service;

import com.z.bookcreat.llm.ModelRouteResult;

public interface ModelRoutingService {

    ModelRouteResult resolve(Long novelId, String sceneCode);
}
