package org.flow.core;

import org.flow.spi.FlowContext;
import org.flow.spi.Step;
import org.flow.spi.StepConfig;
import org.flow.spi.StepResult;

/**
 * 模拟失败步骤执行器（测试用）
 * 用于测试流程引擎的失败处理、状态持久化以及恢复执行（Resume）能力。
 */
public class FailStep implements Step {
    /**
     * 执行逻辑：根据上下文中设置的控制变量决定是否报错。
     * 可以模拟前 N 次执行失败，之后执行成功的情况。
     */
    @Override
    public StepResult execute(FlowContext context, StepConfig config) {
        // 从上下文中获取控制变量：在第几次尝试前保持失败
        Integer failAt = (Integer) context.getVariable("failAt");
        
        // 记录该步骤被调用的次数
        Integer count = (Integer) context.getVariable("failCount");
        if (count == null) count = 0;
        count++;
        context.setVariable("failCount", count);

        // 如果配置了失败阈值且当前尝试次数未达到阈值，则返回失败结果
        if (failAt != null && count <= failAt) {
            return StepResult.fail("Intentional failure", true);
        }
        
        // 达到阈值后模拟执行成功
        return StepResult.success();
    }
}

