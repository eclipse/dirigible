declare module "@dirigible/messaging" {
    interface Queue {
        /**
         * Receives a message from this Message Queue if any or null with the given timeout in milliseconds
         * @param timeout
         */
        receive(timeout:number): string;

        /**
         * Send a message to this Message Queue
         * @param message
         */
        send(message:string);
    }

    interface Topic {
        /**
         * Receives a message from this Message Topic if any or null with the given timeout in milliseconds
         * @param timeout
         */
        receive(timeout:number): string;

        /**
         * Send a message to this Message Queue
         * @param message
         */
        send(message:string);
    }

    module consumer {
        /**
         * Returns an object representing a Message Queue
         * @param destination
         */
        function queue(destination:string): Queue;

        /**
         * Returns an object representing a Message Topic
         * @param destination
         */
        function topic(destination:string): Topic;
    }
    module producer {
        /**
         * Returns an object representing a Message Queue
         * @param destination
         */
        function queue(destination:string): Queue;

        /**
         * Returns an object representing a Message Topic
         * @param destination
         */
        function topic(destination:string): Topic;
    }
}
