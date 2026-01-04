package org.flow.core;

import org.flow.persistence.FlowInstance;
import org.flow.persistence.InMemoryStateStore;
import org.flow.spi.FlowContext;
import org.flow.spi.StepResult;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 流程引擎核心功能单元测试
 */
public class FlowEngineTest {

    /**
     * 测试最基础的顺序流程执行
     * 场景：两个步骤顺序执行，验证上下文变量传递和最终实例状态。
     */
    @Test
    public void testSequentialFlow() throws IOException {
        // 1. 初始化执行器注册表并注册 "log" 类型的执行器
        StepRunnerRegistry registry = new StepRunnerRegistry();
        registry.register("log", new LogStep());
        
        // 2. 初始化持久化组件（使用内存存储）和引擎
        InMemoryStateStore stateStore = new InMemoryStateStore();
        FlowEngine engine = new FlowEngine(registry, stateStore);

        // 3. 构建流程定义：包含两个顺序执行的 log 步骤
        String yaml = "id: test_flow\n" +
                "steps:\n" +
                "  - id: step1\n" +
                "    type: log\n" +
                "    config:\n" +
                "      message: \"Hello from step 1\"\n" +
                "  - id: step2\n" +
                "    type: log\n" +
                "    config:\n" +
                "      message: \"Hello from step 2\"";
        
        // 4. 解析 YAML 定义
        FlowParser parser = new FlowParser();
        FlowDefinition definition = parser.parse(yaml);

        // 5. 启动流程实例
        String instanceId = engine.start(definition, new FlowContext());

        // 6. 结果校验
        Optional<FlowInstance> instanceOpt = stateStore.findById(instanceId);
        assertTrue(instanceOpt.isPresent(), "实例应该被保存在存储中");
        FlowInstance instance = instanceOpt.get();
        
        assertEquals("COMPLETED", instance.getStatus(), "流程应当执行完成");
        assertEquals("step2", instance.getCurrentStepId(), "当前步骤应当更新为最后一个步骤");
        assertEquals("Hello from step 2", instance.getContext().getVariable("lastLog"), "上下文变量应当被正确更新");
    }

    /**
     * 测试流程恢复执行（Resume）能力
     * 场景：步骤在第一次执行时失败，手动修正状态或满足条件后，从失败点恢复并完成整个流程。
     */
    @Test
    public void testResumeFlow() throws IOException {
        // 1. 注册执行器，包含一个专门模拟失败的 "fail" 执行器
        StepRunnerRegistry registry = new StepRunnerRegistry();
        registry.register("log", new LogStep());
        registry.register("fail", new FailStep());
        
        InMemoryStateStore stateStore = new InMemoryStateStore();
        FlowEngine engine = new FlowEngine(registry, stateStore);

        // 2. 构建包含可能失败步骤的流程定义
        String yaml = "id: resume_flow\n" +
                "steps:\n" +
                "  - id: step1\n" +
                "    type: log\n" +
                "    config:\n" +
                "      message: \"First step\"\n" +
                "  - id: step2\n" +
                "    type: fail\n" + // 该步骤在第一次调用时会返回失败
                "  - id: step3\n" +
                "    type: log\n" +
                "    config:\n" +
                "      message: \"Final step\"";
        
        FlowParser parser = new FlowParser();
        FlowDefinition definition = parser.parse(yaml);

        // 3. 启动流程，并在上下文中设置 failAt=1，表示第一次尝试 step2 时会失败
        FlowContext context = new FlowContext();
        context.setVariable("failAt", 1); 
        String instanceId = engine.start(definition, context);

        // 4. 验证流程是否在 step2 处停下并标记为 FAILED
        FlowInstance instance = stateStore.findById(instanceId).get();
        assertEquals("FAILED", instance.getStatus(), "流程初次执行应当失败");
        assertEquals("step2", instance.getCurrentStepId(), "失败应当发生在 step2");
        assertEquals(1, instance.getContext().getVariable("failCount"), "step2 应当被执行了 1 次");

        // 5. 模拟人工修复或满足条件后进行恢复（Resume）
        // 再次调用时，failCount 变为 2，FailStep 将返回成功
        engine.resume(instanceId, definition);

        // 6. 验证流程最终是否顺利完成
        instance = stateStore.findById(instanceId).get();
        assertEquals("COMPLETED", instance.getStatus(), "恢复后的流程应当成功完成");
        assertEquals("step3", instance.getCurrentStepId(), "最终步骤应当是 step3");
        assertEquals(2, instance.getContext().getVariable("failCount"), "step2 总计应当被执行了 2 次");
        assertEquals("Final step", instance.getContext().getVariable("lastLog"), "后续步骤应当被正确执行");
    }

    /**
     * 测试流程解析时的合法性校验
     * 场景：验证当 YAML 配置缺失必要字段或存在逻辑错误（如 ID 重复）时，解析器能正确报错。
     */
    @Test
    public void testValidation() {
        FlowParser parser = new FlowParser();
        
        // 验证场景 1：缺失流程 ID 应当报错
        assertThrows(IllegalArgumentException.class, () -> parser.parse("steps: []"));
        
        // 验证场景 2：存在重复的步骤 ID 应当报错
        String yaml = "id: dup_flow\n" +
                "steps:\n" +
                "  - id: s1\n" +
                "    type: log\n" +
                "  - id: s1\n" + // 重复的 ID
                "    type: log";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(yaml));
    }
}

