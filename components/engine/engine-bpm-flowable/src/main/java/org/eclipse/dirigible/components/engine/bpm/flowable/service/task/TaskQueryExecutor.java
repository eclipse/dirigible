package org.eclipse.dirigible.components.engine.bpm.flowable.service.task;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.BpmService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public record TaskQueryExecutor(BpmService bpmService) {

    public List<Task> findTasks(String processInstanceId, Type type) {
        List<Task> tasks = Collections.emptyList();
        if (Type.CANDIDATE_GROUPS.equals(type)) {
            tasks = getTaskService().createTaskQuery()
                                    .processInstanceId(processInstanceId)
                                    .taskCandidateGroupIn(UserFacade.getUserRoles())
                                    .list();

        } else if (Type.ASSIGNEE.equals(type)) {
            tasks = getTaskService().createTaskQuery()
                                    .processInstanceId(processInstanceId)
                                    .taskAssignee(UserFacade.getName())
                                    .list();
        }
        return tasks;
    }

    private TaskService getTaskService() {
        return bpmService.getBpmProviderFlowable()
                         .getProcessEngine()
                         .getTaskService();
    }

    public enum Type {
        ASSIGNEE("assignee"), CANDIDATE_GROUPS("groups");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public static Type fromString(String type) {
            for (Type enumValue : Type.values()) {
                if (enumValue.type.equals(type)) {
                    return enumValue;
                }
            }
            throw new IllegalArgumentException("Unknown enum type: " + type);
        }
    }
}
