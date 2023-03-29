declare module "@dirigible/log" {
    module logging {
        /**
         * Returns the Logger object by this name
         * @param loggerName
         */
        function getLogger(loggerName): Logger;
    }

    interface Logger {
        /**
         * Logs the message with the INFO log level
         * @param message
         * @param args
         */
        info(message, args?);

        /**
         * Logs the message with the WARN log level
         * @param message
         * @param args
         */
        warn(message, args?);

        /**
         * Logs the message with the ERROR log level
         * @param message
         * @param args
         */
        error(message, args?);

        /**
         * Logs the message with the DEBUG log level
         * @param message
         * @param args
         */
        debug(message, args?);

        /**
         * Logs the message with the TRACE log level
         * @param message
         * @param args
         */
        trace(message, args?);

        /**
         * Logs the message with the provided log level and optional message parameters
         * @param message
         * @param args
         */
        log(message, args?);

        /**
         * Logs the error with the stack trace with the INFO log level
         * @param message
         * @param args
         */
        infoError(message, args?);

        /**
         * Logs the error with the stack trace with the WARN log level
         * @param message
         * @param args
         */
        warnError(message, args?);

        /**
         * Logs the error with the stack trace with the ERROR log level
         * @param message
         * @param args
         */
        errorError(message, args?);

        /**
         * Logs the error with the stack trace with the DEBUG log level
         * @param message
         * @param args
         */
        debugError(message, args?);

        /**
         * Logs the error with the stack trace with the TRACE log level
         * @param message
         * @param args
         */
        traceError(message, args?);

        /**
         * Sets the log level ('INFO', 'WARN', 'ERROR', 'DEBUG', 'TRACE')
         * @param level
         */
        setLevel(level);

    }
}
