package org.flow.core;

import org.flow.spi.FlowContext;
import org.flow.spi.Step;
import org.flow.spi.StepConfig;
import org.flow.spi.StepResult;

/**
 * 日志打印步骤执行器（测试用）
 * 用于验证流程是否执行到了该步骤，并将执行结果记录在上下文中。
 */
public class LogStep implements Step {
    /**
     * 执行逻辑：从配置中读取 message 并打印
     * 同时将消息存入上下文变量 "lastLog" 中，供后续步骤或断言使用。
     */
    @Override
    public StepResult execute(FlowContext context, StepConfig config) {
        // 1. 从步骤私有配置中获取要打印的消息
        String message = (String) config.getConfig().get("message");
        
        // 2. 模拟业务处理：控制台输出
        System.out.println("[LogStep] " + message);
        
        // 3. 将执行痕迹存入流程上下文，方便测试断言验证
        context.setVariable("lastLog", message);
        
        // 4. 返回成功结果
        return StepResult.success();
    }
}

