declare module "@dirigible/kafka" {
    module producer {
        /**
         * Returns an object representing a Kafka Topic
         * @param destination
         * @param configuration
         */
        function topic(destination, configuration): Topic

        /**
         * Closes the Producer
         */
        function close();

        interface Topic {
            /**
             * Send a message record by a key and value to a Kafka Topic
             * @param key
             * @param value
             */
            send(key, value);
        }
    }

    module consumer {
        /**
         *  Returns an object representing a Kafka Topic
         * @param destination
         * @param configuration
         */
        function topic(destination: string, configuration): Topic;

        interface Topic {
            /**
             * Receives a message from this Kafka Topic if any with the given handler and timeout in milliseconds
             * @param handler
             * @param timeout
             */
            startListening(handler, timeout);

            /**
             * Stops listening for new messages
             */
            stopListening();
        }
    }


}
