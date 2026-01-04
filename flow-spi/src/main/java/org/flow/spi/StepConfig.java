package org.flow.spi;

import lombok.Data;
import java.util.Map;

/**
 * 步骤配置类
 * 定义了流程定义中单个步骤的元数据
 */
@Data
public class StepConfig {
    /** 步骤唯一标识 */
    private String id;
    /** 步骤类型，对应执行器注册名称 */
    private String type;
    /** 步骤私有配置参数 */
    private Map<String, Object> config;
}

