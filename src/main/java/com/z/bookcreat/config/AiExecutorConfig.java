package com.z.bookcreat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AiExecutorConfig {

    private final AppLlmProperties appLlmProperties;

    @Bean(name = "aiTaskExecutor")
    public Executor aiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appLlmProperties.getExecutor().getCorePoolSize());
        executor.setMaxPoolSize(appLlmProperties.getExecutor().getMaxPoolSize());
        executor.setQueueCapacity(appLlmProperties.getExecutor().getQueueCapacity());
        executor.setThreadNamePrefix(appLlmProperties.getExecutor().getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
