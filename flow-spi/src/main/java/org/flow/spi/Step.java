package org.flow.spi;

/**
 * 步骤执行器接口
 * 所有具体的业务步骤都需要实现此接口
 */
public interface Step {
    /**
     * 执行步骤逻辑
     * @param context 流程上下文，用于传递变量
     * @param config 步骤配置，包含外部注入的参数
     * @return 执行结果
     */
    StepResult execute(FlowContext context, StepConfig config);
}

