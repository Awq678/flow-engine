package org.flow.spi;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程执行上下文
 * 用于在步骤之间传递数据
 */
@Data
public class FlowContext {
    /** 流程定义ID */
    private String flowId;
    /** 流程实例ID */
    private String instanceId;
    /** 当前执行到的步骤ID */
    private String currentStepId;
    /** 流程变量存储 */
    private Map<String, Object> variables = new HashMap<>();

    /**
     * 设置变量
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * 获取变量
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }
}

