declare module "@dirigible/messaging" {
    interface Queue {
        receive(timeout): string;

        send(message);
    }

    interface Topic {
        receive(timeout): string;

        send(message);
    }

    module consumer {
        function queue(destination): Queue;

        function topic(destination): Topic;
    }
    module producer {
        function queue(destination): Queue;

        function topic(destination): Topic;
    }
}