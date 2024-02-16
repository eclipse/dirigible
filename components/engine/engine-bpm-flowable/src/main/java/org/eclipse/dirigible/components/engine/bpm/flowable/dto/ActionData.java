package org.eclipse.dirigible.components.engine.bpm.flowable.dto;

public class ActionData {

    private String action;

    public String getAction() {
        return action;
    }

    public enum Action {
        RETRY("RETRY");

        private final String actionName;

        Action(String actionName) {
            this.actionName = actionName;
        }

        public String getActionName() {
            return actionName;
        }

    }
}
