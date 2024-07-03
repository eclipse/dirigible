package org.eclipse.dirigible.components.engine.bpm.flowable.endpoint;

import java.util.Map;

class TaskVariablesDTO {

    private final Map<String, Object> variables;

    public TaskVariablesDTO(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}
