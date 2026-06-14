package com.z.bookcreat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.llm")
public class AppLlmProperties {

    private int defaultTimeoutMs = 60000;

    private Executor executor = new Executor();

    @Data
    public static class Executor {
        private int corePoolSize = 2;
        private int maxPoolSize = 4;
        private int queueCapacity = 32;
        private String threadNamePrefix = "ai-gen-";
    }
}
