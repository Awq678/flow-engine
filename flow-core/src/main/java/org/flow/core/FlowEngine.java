package org.flow.core;

import lombok.extern.slf4j.Slf4j;
import org.flow.persistence.FlowInstance;
import org.flow.persistence.StateStore;
import org.flow.spi.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 流程引擎核心类
 * 负责流程的启动、执行和状态调度
 */
@Slf4j
public class FlowEngine {
    private final StepRunnerRegistry registry;
    private final StateStore stateStore;

    public FlowEngine(StepRunnerRegistry registry, StateStore stateStore) {
        this.registry = registry;
        this.stateStore = stateStore;
    }

    /**
     * 启动一个新流程
     * @param definition 流程定义
     * @param initialContext 初始上下文
     * @return 流程实例ID
     */
    public String start(FlowDefinition definition, FlowContext initialContext) {
        String instanceId = UUID.randomUUID().toString();
        FlowInstance instance = new FlowInstance();
        instance.setInstanceId(instanceId);
        instance.setFlowId(definition.getId());
        instance.setStatus("RUNNING");
        instance.setContext(initialContext != null ? initialContext : new FlowContext());
        instance.getContext().setFlowId(definition.getId());
        instance.getContext().setInstanceId(instanceId);
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());

        stateStore.save(instance);
        log.info("Started flow instance: {}, flowId: {}", instanceId, definition.getId());
        execute(instance, definition);
        return instanceId;
    }

    /**
     * 恢复执行一个失败或中断的流程
     * @param instanceId 流程实例ID
     * @param definition 流程定义
     */
    public void resume(String instanceId, FlowDefinition definition) {
        Optional<FlowInstance> instanceOpt = stateStore.findById(instanceId);
        if (instanceOpt.isPresent()) {
            FlowInstance instance = instanceOpt.get();
            if ("COMPLETED".equals(instance.getStatus())) {
                log.warn("Flow instance {} is already COMPLETED", instanceId);
                return;
            }
            instance.setStatus("RUNNING");
            instance.setUpdateTime(LocalDateTime.now());
            stateStore.update(instance);
            log.info("Resuming flow instance: {}, currentStep: {}", instanceId, instance.getCurrentStepId());
            execute(instance, definition);
        } else {
            throw new RuntimeException("Flow instance not found: " + instanceId);
        }
    }

    /**
     * 执行流程实例
     */
    private void execute(FlowInstance instance, FlowDefinition definition) {
        FlowContext context = instance.getContext();
        boolean shouldExecute = (instance.getCurrentStepId() == null);
        
        for (StepConfig stepConfig : definition.getSteps()) {
            // 如果已经找到当前步骤，或者之前没有设置当前步骤（即从头开始）
            if (!shouldExecute) {
                if (stepConfig.getId().equals(instance.getCurrentStepId())) {
                    shouldExecute = true;
                } else {
                    continue;
                }
            }

            instance.setCurrentStepId(stepConfig.getId());
            instance.setUpdateTime(LocalDateTime.now());
            stateStore.update(instance);

            Step runner = registry.getRunner(stepConfig.getType());
            if (runner == null) {
                log.error("No runner found for type: {} in step: {}", stepConfig.getType(), stepConfig.getId());
                handleFailure(instance, "No runner found for type: " + stepConfig.getType());
                return;
            }

            try {
                log.info("Executing step: {} [{}]", stepConfig.getId(), stepConfig.getType());
                StepResult result = runner.execute(context, stepConfig);
                if (!result.isSuccess()) {
                    log.error("Step {} failed: {}", stepConfig.getId(), result.getMessage());
                    handleFailure(instance, result.getMessage());
                    return;
                }
            } catch (Exception e) {
                log.error("Error executing step " + stepConfig.getId(), e);
                handleFailure(instance, e.getMessage());
                return;
            }
        }

        instance.setStatus("COMPLETED");
        instance.setUpdateTime(LocalDateTime.now());
        stateStore.update(instance);
        log.info("Flow instance {} COMPLETED", instance.getInstanceId());
    }

    private void handleFailure(FlowInstance instance, String message) {
        instance.setStatus("FAILED");
        instance.setUpdateTime(LocalDateTime.now());
        // 可以在 context 中记录错误信息
        instance.getContext().setVariable("lastError", message);
        stateStore.update(instance);
    }
}

