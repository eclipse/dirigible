declare module "@dirigible/elasticsearch" {
    module client {
        /**
         *
         */
        function getClient(): Client;
    }

    /**
     *
     */
    interface Client {
        /**
         *
         */
        documents: DocumentApi;
        /**
         *
         */
        indexes: IndexAPI;

        /**
         *
         */
        close();
    }

    /**
     *
     */
    interface DocumentApi {
        /**
         *
         * @param index
         * @param id
         * @param documentSource
         */
        index(index, id, documentSource);

        /**
         *
         * @param index
         * @param id
         */
        get(index, id): string;

        /**
         *
         * @param index
         * @param id
         */
        exists(index, id): boolean;

        /**
         *
         * @param index
         * @param id
         */
        delete(index, id);
    }

    /**
     *
     */
    interface IndexAPI {
        /**
         *
         */
        create();

        /**
         *
         */
        delete();

        /**
         *
         * @param name
         */
        exists(name: string): boolean;
    }
}
