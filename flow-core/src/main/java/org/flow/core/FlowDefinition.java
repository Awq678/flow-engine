package org.flow.core;

import lombok.Data;
import org.flow.spi.StepConfig;
import java.util.List;

/**
 * 流程定义类
 * 对应 YAML/JSON 配置文件
 */
@Data
public class FlowDefinition {
    /** 流程定义唯一标识 */
    private String id;
    /** 流程步骤列表（顺序执行） */
    private List<StepConfig> steps;
}

