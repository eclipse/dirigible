package org.eclipse.dirigible.components.engine.bpm.flowable.dto;

public class TaskActionData {
    private String action;

    public String getAction() {
        return action;
    }

    public enum TaskAction {

        CLAIM("CLAIM"),

        UNCLAIM("UNCLAIM");

        private final String actionName;

        TaskAction(String actionName) {
            this.actionName = actionName;
        }

        public String getActionName() {
            return actionName;
        }
    }
}
