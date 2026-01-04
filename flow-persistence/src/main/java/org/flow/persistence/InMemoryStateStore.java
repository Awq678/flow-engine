package org.flow.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的状态存储实现
 * 仅用于开发和单机测试场景
 */
public class InMemoryStateStore implements StateStore {
    private final Map<String, FlowInstance> storage = new ConcurrentHashMap<>();

    @Override
    public void save(FlowInstance instance) {
        storage.put(instance.getInstanceId(), instance);
    }

    @Override
    public Optional<FlowInstance> findById(String instanceId) {
        return Optional.ofNullable(storage.get(instanceId));
    }

    @Override
    public void update(FlowInstance instance) {
        storage.put(instance.getInstanceId(), instance);
    }
}

