declare module "@dirigible/rabbitmq" {
    module consumer {
        function startListening(queue, handler);

        function stopListening(queue, handler);
    }
    module producer {
        function send(queue, message);
    }
}
