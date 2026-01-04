package org.flow.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.flow.spi.StepConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 流程定义解析器
 * 支持将 YAML 字符串或流解析为 FlowDefinition 对象，并进行基本合法性校验
 */
public class FlowParser {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * 从输入流解析
     */
    public FlowDefinition parse(InputStream inputStream) throws IOException {
        FlowDefinition definition = mapper.readValue(inputStream, FlowDefinition.class);
        validate(definition);
        return definition;
    }

    /**
     * 从字符串解析
     */
    public FlowDefinition parse(String yaml) throws IOException {
        FlowDefinition definition = mapper.readValue(yaml, FlowDefinition.class);
        validate(definition);
        return definition;
    }

    /**
     * 校验流程定义的合法性
     */
    private void validate(FlowDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Flow definition cannot be null");
        }
        if (definition.getId() == null || definition.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Flow id is required");
        }
        if (definition.getSteps() == null || definition.getSteps().isEmpty()) {
            throw new IllegalArgumentException("Flow must have at least one step");
        }

        Set<String> stepIds = new HashSet<>();
        for (StepConfig step : definition.getSteps()) {
            if (step.getId() == null || step.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("Step id is required");
            }
            if (step.getType() == null || step.getType().trim().isEmpty()) {
                throw new IllegalArgumentException("Step type is required for step: " + step.getId());
            }
            if (!stepIds.add(step.getId())) {
                throw new IllegalArgumentException("Duplicate step id: " + step.getId());
            }
        }
    }
}

