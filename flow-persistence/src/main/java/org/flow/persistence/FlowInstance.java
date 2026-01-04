package org.flow.persistence;

import lombok.Data;
import org.flow.spi.FlowContext;
import java.time.LocalDateTime;

/**
 * 流程运行实例
 * 记录流程在运行过程中的状态和上下文
 */
@Data
public class FlowInstance {
    /** 实例唯一标识 */
    private String instanceId;
    /** 关联的流程定义ID */
    private String flowId;
    /** 当前执行到的步骤ID */
    private String currentStepId;
    /** 实例状态：RUNNING, COMPLETED, FAILED */
    private String status;
    /** 流程上下文数据 */
    private FlowContext context;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 最后更新时间 */
    private LocalDateTime updateTime;
}

