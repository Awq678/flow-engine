package org.flow.spi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 步骤执行结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StepResult {
    /** 是否执行成功 */
    private boolean success;
    /** 结果消息 */
    private String message;
    /** 是否可重试 */
    private boolean retryable;

    /**
     * 快速创建成功结果
     */
    public static StepResult success() {
        return new StepResult(true, "Success", false);
    }

    /**
     * 快速创建失败结果
     * @param message 错误消息
     * @param retryable 是否允许重试
     */
    public static StepResult fail(String message, boolean retryable) {
        return new StepResult(false, message, retryable);
    }
}

