package org.flow.core;

import org.flow.spi.Step;
import java.util.HashMap;
import java.util.Map;

/**
 * 步骤执行器注册中心
 */
public class StepRunnerRegistry {
    private final Map<String, Step> runners = new HashMap<>();

    /**
     * 注册执行器
     * @param type 类型标识（如 "http", "db", "log"）
     * @param step 执行器实现
     */
    public void register(String type, Step step) {
        runners.put(type, step);
    }

    /**
     * 获取执行器
     */
    public Step getRunner(String type) {
        return runners.get(type);
    }
}

