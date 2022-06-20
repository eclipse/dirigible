declare module "@dirigible/job" {
    module scheduler {
        function getJobs(): Job[]

        function getJob(name: string): Job

        function enableJob(name: string)

        function disableJob(name: string)

        function triggerJob(name: string, parameters: Object)

    }


    class Job {

        /**
         * Returns the name of the job
         */
        getName(): string

        /**
         * Returns the group of the group
         */
        getGroup(): string

        /**
         * Returns the clazz of the job
         */
        getClazz(): string

        /**
         * Returns the description of the job
         */
        getDescription(): string

        /**
         * Returns the expression of the job
         */
        getExpression(): string

        /**
         * Returns the handler of the job
         */
        getHandler(): string

        /**
         * Returns the engine of the job
         */
        getEngine(): string

        /**
         * Returns the singleton of the job
         */
        getSingleton(): string

        /**
         * Returns the enabled of the job
         */
        getEnabled(): string

        /**
         * Returns the createdBy of the job
         */
        getCreatedBy(): string

        /**
         * Returns the createdAt of the job
         */
        getCreatedAt(): string

        /**
         * Returns the parameters of the job
         */
        getParameters(): JobParameters

        /**
         * Enable execution of a Job
         */
        enable()

        /**
         * Disable execution of a Job
         */
        disable()

        /**
         * Trigger execution of a Job
         */
        trigger(parameters: Object)


    }

    class JobParameters {

        /**
         * Returns the get of the job parameters
         */
        get(i: number): JobParameter

        /**
         * Returns the count of the job parameters
         */
        count(): number

    }

    class JobParameter {

        /**
         * Returns the name of the job parameter
         */
        getName(): string

        /**
         * Returns the description of the job parameter
         */
        getDescription(): string

        /**
         * Returns the type of the job parameter
         */
        getType(): string

        /**
         * Returns the default value of the job parameter
         */
        getDefaultValue(): string

        /**
         * Returns the choices of the job parameter
         */
        getChoices(): string


    }

}
