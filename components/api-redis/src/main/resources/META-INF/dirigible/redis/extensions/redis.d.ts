declare module "@dirigible/redis" {
    module client {
        function getClient(): Client;

        interface Client {
            /**
             * Append a string to the value of a key.
             * @param key
             * @param value
             */
            append(key: string, value: string);

            /**
             * Count the number of set bits in a string.
             * @param key
             */
            bitcount(key: string);

            /**
             * Decrements the integer value of a key by one. Uses 0 as initial value if the key doesn't exist.
             * @param key
             */
            decr(key: string);

            /**
             * Deletes one or more keys.
             * @param key
             */
            del(key: string);

            /**
             * Determine whether one or more keys exist.
             * @param key
             */
            exists(key): boolean;

            /**
             * Returns the string value of a key.
             * @param key
             */
            get(key);

            /**
             * Increments the integer value of a key by one. Uses 0 as initial value if the key doesn't exist.
             * @param key
             */
            incr(key);

            /**
             * Returns all key names that match a pattern.
             * @param pattern
             */
            keys(pattern);

            /**
             * Sets the string value of a key, ignoring its type. The key is created if it doesn't exist.
             * @param key
             * @param value
             */
            set(key, value);

            /**
             * Returns an element from a list by its index.
             * @param key
             * @param index
             */
            lindex(key, index);

            /**
             * Returns the length of a list.
             * @param key
             */
            llen(key);

            /**
             * Removes and returns the first elements of the list stored at key.
             * @param key
             */
            lpop(key);

            /**
             * Prepends one or more elements to a list. Creates the key if it doesn't exist.
             * @param key
             * @param value
             */
            lpush(key, value);

            /**
             * Returns a range of elements from a list.
             * @param key
             * @param start
             * @param stop
             */
            lrange(key, start, stop);

            /**
             * Returns and removes the last elements of a list. Deletes the list if the 1st elements was popped.
             * @param key
             */
            rpop(key);

            /**
             * Appends one or more elements to a list. Create the key if it doesn't exist.
             * @param key
             * @param value
             */
            rpush(key, value);
        }
    }
}
