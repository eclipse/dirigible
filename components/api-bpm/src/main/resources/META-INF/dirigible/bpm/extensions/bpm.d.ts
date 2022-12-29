declare module "@dirigible/bpm" {
	
    module deployer {
        function deployProcess(location: string): string

        function undeployProcess(processId: string);
    }
    
    module process {
        function getVariable(processInstanceId: string, variableName: string): string

        function start(key: string, parameters: string): string

        function getProcessEngine(): ProcessEngine

        function getExecutionContext(): ExecutionContext;

        function setVariable(processInstanceId: string, variableName: string, variableValue: string)

        function removeVariable(processInstanceId: string, variableName: string)
    }
    
    module tasks {
        /**
         * Returns list of tasks
         */
        function list(): string[]

        /**
         * Set the task's variables
         *
         * @param taskId the task id
         */
        function getTaskVariables(taskId: string): string[]

        /**
         * Set a variable in the process execution context
         * @param taskId
         * @param variables
         */
        function setTaskVariables(taskId: string, variables: string)

        /**
         * Complete the task with variables
         *
         * @param taskId the task id
         * @param variables serialized as JSON string
         */
        function completeTask(taskId, variables: string)
    }

    class ProcessEngine {

    }

    class ExecutionContext {
        /**
         * Returns the id from the execution context
         */
        getId(): string

        /**
         * Returns the isActive flag from the execution context
         */
        isActive(): boolean

        /**
         * Returns the isScope flag from the execution context
         */
        isScope(): boolean

        /**
         * Returns the isConcurrent flag from the execution context
         */
        isConcurrent(): boolean

        /**
         * Returns the isEnded flag from the execution context
         */
        inEnded(): boolean

        /**
         * Returns the isEventScope flag from the execution context
         */
        isEventScope(): boolean

        /**
         * Returns the isMultiInstanceRoot flag from the execution context
         */
        isMultiInstanceRoot(): boolean

        /**
         * Returns the isCountEnabled flag from the execution context
         */
        isCountEnabled(): boolean

        /**
         * Returns the suspensionState flag from the execution context
         */
        suspensionState(): string

        /**
         * Returns the start time from the execution context
         */
        startTime(): string

        /**
         * Returns the event subscription count from the execution context
         */
        eventSubscriptionCount(): string

        /**
         * Returns the task count from the execution context
         */
        taskCount(): string

        /**
         * Returns the job count from the execution context
         */
        jobCount(): string

        /**
         * Returns the timer job count from the execution context
         */
        timerJobCount(): string

        /**
         * Returns the suspended job count from the execution context
         */
        suspendedJobCount(): string

        /**
         * Returns the dead letter job count from the execution context
         */
        deadLetterJobCount(): string

        /**
         * Returns the variable count from the execution context
         */
        variableCount(): string

        /**
         * Returns the identity link count from the execution context
         */
        identityLinkCount(): string

        /**
         * Returns the process definition id from the execution context
         */
        processDefinitionId(): string

        /**
         * Returns the process definition key from the execution context
         */
        processDefinitionKey(): string

        /**
         * Returns the activity id from the execution context
         */
        activityId(): string

        /**
         * Returns the process instance id from the execution context
         */
        processInstanceId(): string

        /**
         * Returns the parent process id from the execution context
         */
        parentId(): string

        /**
         * Returns the root process instance id from the execution context
         */
        rootProcessInstanceId(): string

        /**
         * Returns the forcedUpdate flag from the execution context
         */
        forcedUpdate(): string

        /**
         * Returns the revision from the execution context
         */
        revision(): string

        /**
         * Returns the tenant id from the execution context
         */
        tenantId(): string
    }
}
