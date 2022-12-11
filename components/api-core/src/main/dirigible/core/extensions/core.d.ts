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
    module globals {
        /**
         * Returns the value per key from the global parameters
         * @param name
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
}
