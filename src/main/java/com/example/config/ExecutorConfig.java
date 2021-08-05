package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = 2 * CORE_POOL_SIZE;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int QUERY_CAPACITY = 128;
    private static final int AVAIL_TERMINATION_MILLIS = 60;
    private static final String THREAD_NAME_PREFIX = "TaskExecutor";

    @Bean("executor")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(CORE_POOL_SIZE);
        // 最大线程数
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        // 允许线程的空闲时间
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        // 缓冲队列
        executor.setQueueCapacity(QUERY_CAPACITY);
        // 线程前缀
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 线程中任务的等待时间
        executor.setAwaitTerminationMillis(AVAIL_TERMINATION_MILLIS);
        // 线程对拒绝任务的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程关闭的时候等待所有任务完成后继续销毁其它Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);

        return executor;
    }

}
