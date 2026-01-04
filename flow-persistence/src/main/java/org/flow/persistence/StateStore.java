package org.flow.persistence;

import java.util.Optional;

/**
 * 状态持久化接口
 * 负责流程实例的保存、查询和更新
 */
public interface StateStore {
    /**
     * 保存新实例
     */
    void save(FlowInstance instance);

    /**
     * 根据ID查找实例
     */
    Optional<FlowInstance> findById(String instanceId);

    /**
     * 更新实例状态
     */
    void update(FlowInstance instance);
}

