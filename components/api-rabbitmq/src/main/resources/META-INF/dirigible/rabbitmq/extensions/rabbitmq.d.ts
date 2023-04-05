declare module "@dirigible/rabbitmq" {
    module consumer {
        /**
         * Start listening on given queue and destination.
         * @param queue   the queue being used
         * @param handler the destination for the message
         */
        function startListening(queue: string, handler: string);

        /**
         * Stop listening on given queue and destination.
         * @param queue   the queue being used
         * @param handler the destination for the message
         */
        function stopListening(queue: string, handler: string);
    }
    module producer {
        /**
         * Send message to given queue.
         *
         * @param queue   the queue being used
         * @param message the message to be delivered
         */
        function send(queue: string, message: string);
    }
}
