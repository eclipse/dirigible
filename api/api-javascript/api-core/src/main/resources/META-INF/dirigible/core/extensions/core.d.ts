declare module "@dirigible/core" {
    module configurations {
        /**
         * Returns the value for the specified key, or the default value
         * @param key
         * @param defaultValue
         */
        function get(key, defaultValue?): string;

        /**
         * Sets a value, for the specified key
         * @param key
         * @param value
         */
        function set(key, value);

        function remove(key);

        /**
         * Returns an arrays of keys
         */
        function getKeys(): string[];

        /**
         * Loads a configuration from a properties file at path
         * @param path
         */
        function load(path: string);

        /**
         * Updates the loaded configurations
         */
        function update();

        function getOS();

        function isOSWindows(): boolean;

        function isOSMac(): boolean;

        function isOSLinux(): boolean;

        function isOSSolaris(): boolean;
    }
    module console {
        function log(message);

        function error(message);

        function info(message);

        function warn(message);

        function debug(message);

        function trace(message);

        function stringify(message);
    }
    module context {
        /**
         * Returns the value per key from the context parameters
         * @param key
         */
        function get(key: string): string;

        /**
         * Sets the value per key to the context parameters
         * @param name
         * @param value
         */
        function set(name, value);
    }
    module destination {
        /**
         * Returns an object representing the destination
         * @param name
         */
        function get(name): string;

        /**
         * Sets the destination object under the given name
         * @param name
         * @param destination
         */
        function set(name, destination);
    }
    module env {
        /**
         * Returns the value per key from the environments variables
         * @param name
         */
        function get(name): string;

        /**
         * Returns the list of the environments variables in JSON formatted string
         */
        function list(): string[];
    }
    module exec {
        /**
         * Executes the commandLine string and returns the result from the execution or exception message. Passing an object as toAdd parameter sets the corresponding variables. toRemove parameter is used to unset the variables
         * @param commandLine
         * @param toAdd
         * @param toRemove
         */
        function exec(commandLine, toAdd, toRemove);
    }
    module extensions {
        /**
         * Returns an array of the extensions names for the specified extension point
         * @param extensionPoint
         */
        function getExtensions(extensionPoint): string[];

        /**
         *Returns an array of the extension points names
         */
        function getExtensionPoints(): string[];
    }
    module globals {
        /**
         * Returns the value per key from the global parameters
         * @param key
         */
        function get(key: string): string;

        /**
         * Sets the value per key to the global parameters
         * @param name
         * @param value
         */
        function set(name, value);

        /**
         * Returns list of globals
         */
        function list(): string[];
    }
    module threads {
        /**
         * Creates a new thread by a callback function and a name
         * @param runnable
         * @param name
         */
        function create(runnable, name): Thread;

        /**
         * Suspends the execution of the current thread
         * @param mills
         */
        function sleep(mills);

        /**
         * Returns the current thread
         */
        function current(): Thread;

        /**
         * Waits a given period of time until continuing the execution of the current thread or until another thread call notify of this object
         * @param mills
         */
        function wait(mills);

        /**
         * Wakes up a single thread waiting for this object
         */
        function notify();

        /**
         * Wakes up all the threads waiting for this object
         */
        function notifyAll();

    }

    interface Thread {
        /**
         * Returns internal thread
         */
        internalThread();

        getInternalObject();

        /**
         * Starts the thread
         */
        start();

        /**
         * Interrupts the execution of a thread
         */
        interrupt();

        /**
         * Waits this thread to die
         */
        join();

        /**
         * Returns the ID of the thread
         */
        getID(): number;

        /**
         * Returns the Name of the thread
         */
        getName(): string;

        /**
         * Returns true if the thread is still alive
         */
        isAlive(): boolean;

    }
}
