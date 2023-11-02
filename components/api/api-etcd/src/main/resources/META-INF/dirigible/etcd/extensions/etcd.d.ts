// @ts-ignore
class byte {
}

declare module "@dirigible/etcd" {
    module client {
        function getClient(): Client;
    }

    interface Client {
        /**
         * Puts a key-value pair to the etcd storage where the value should be a string
         * @param key
         * @param value
         */
        putStringValue(key: string, value: string);

        /**
         * Puts a key-value pair to the etcd storage where the value should be a byte array
         * @param key
         * @param value
         */
        putByteArrayValue(key: string, value: byte[]);

        /**
         * Returns an object representing an Etcd Header
         * @param key
         */
        getHeader(key: string): Header;

        /**
         * Returns a key-value object with string value
         */

        getKvsStringValue(key: string):string;

        /**
         * Returns a key-value object with byte array value
         * @param key
         */
        getKvsByteArrayValue(key: string);

        /**
         * Returns the number of keys if the get method is for range
         * @param key
         */
        getCount(key): number;

        /**
         * Deletes a key-value pair
         * @param key
         */
        delete(key);

        get(key): GetResponse;
    }

    interface GetResponse {
        getHeader(): Header;

        getKvsString(): string;

        getKvsByteArray(): string;

        getCount(): number;
    }

    interface Header {
        /**
         * Returns the revision of the header
         */
        getRevision(): number;

        /**
         * Returns the cluster id of the header
         */
        getClusterId(): number;

        /**
         * Returns the member id of the header
         */
        getMemberId(): number;

        /**
         * Returns the raft term of the header
         */
        getRaftTerm(): number;
    }

}

