import { rs } from "@dirigible/http";
import { tasks as tasksService, process } from "@dirigible/bpm";
const BpmModule = Java.type("org.eclipse.dirigible.bpm.api.BpmModule");

rs.service()
    .resource("tasks")
    .get(getUserTasks)
    .execute();

function getUserTasks(ctx, request, response) {
    const tasksJson = BpmModule.getProcessEngineProvider().getTasks();

    const tasks = JSON.parse(tasksJson).map(t => {
        return {
            taskId: t.id,
            operationType: t.name,
            createdAt: t.createTime,
            finishedAt: t.claimTime,
            email: t.assignee || "support",
            isFinished: (t.suspensionState !== 1) ? "Yes" : "No"
        }
    })

    const tasksRes = JSON.stringify(tasks);
    response.print(tasksRes);
}