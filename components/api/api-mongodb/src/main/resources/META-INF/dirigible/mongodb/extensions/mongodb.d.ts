declare module "@dirigible/mongodb" {
    module client {
        /**
         * Returns an object representing a MongoDB Client
         */
        function getClient(): Client;

        /**
         * Creates an empty DBObject
         */
        function createBasicDBObject(): DBObject;

        /**
         * Client
         */
        interface Client {
            /**
             * Returns an object representing a MongoDB Database
             * @param name
             */
            getDB(name: string): DB;
        }

        interface DB {
            /**
             * Returns an object representing a MongoDB Collection
             * @param name
             */
            getCollection(name: string): DBCollection;
        }

        interface DBCollection {
            /**
             * Inserts a DBObject to a Collection
             */
            insert(dbObject: DBObject): DBCursor;

            /**
             * Returns a DBCursor based on the optional DBObjects parameters
             * @param query
             * @param projection
             */
            find(query?, projection?): DBCursor;

            /**
             * Returns a single DBObject based on the optional DBObjects parameters and sort
             * @param query
             * @param projection
             * @param sort
             */
            findOne(query?, projection?, sort?): DBObject;

            /**
             * Returns a single DBObject by id and the optional DBObject projection parameter
             * @param id
             * @param projection
             */

            findOneByID(id, projection?): DBObject;

            /**
             * Returns the count of matching DBObjects by the optional DBObject query parameter
             * @param query
             */
            count(query): number;

            /**
             * Creates an index by keys and optional DBObject options parameters
             * @param key
             * @param options
             */
            createIndex(key, options);

            createIndexForField(name);

            distinct(name, query);

            /**
             * Drops the index by the DBObeject parameter
             * @param index
             */
            dropIndex(index);

            /**
             * Drops the index by the name parameter
             * @param name
             */
            dropIndexByName(name);

            /**
             * Drops all the indexes
             */
            dropIndexes();

            /**
             * Removes the objects matching the query DBObject parameter
             * @param query
             */
            remove(query:string);

            /**
             * Renames the collection by the newName parameter
             * @param newName
             */
            rename(newName:string);

            /**
             * Saves a DBObject to a Collection depends on whether _id of the object is provided or not
             * @param dbObject
             */
            save(dbObject: DBObject);

            /**
             * Updates the matching objects by query parameter with update value and optional upsert and multi flags
             * @param query
             * @param update
             * @param upsert
             * @param multi
             */
            update(query, update, upsert, multi);

            /**
             * Updates the matching objects by query parameter with update value
             * @param query
             * @param update
             */
            updateMulti(query, update);

            /**
             * Calculate the next id for this collection in case of integer sequence is used
             */
            getNextId();

            /**
             * Generate UUID to be used as id
             */
            generateUUID(): number;
        }

        interface DBCursor {
            /**
             * Returns a single DBObject
             */
            one(): DBObject;

            /**
             * Sets the batch size
             * @param numberOfElements
             */
            batchSize(numberOfElements): number;

            /**
             * Gets the batch size
             */
            getBatchSize(): number;

            /**
             * Gets the corresponding DBCollection
             */
            getCollection(): DBCollection;

            /**
             * Gets the cursor id
             */
            getCursorId();

            /**
             * Returns as a keys DBObject
             */
            getKeysWanted(): string[];

            /**
             * Gets the results limit
             */
            getLimit();

            /**
             * Closes the cursor
             */
            close();

            /**
             * Returns true if there is more objects
             */
            hasNext(): boolean;

            /**
             * Returns the next single DBObject
             */
            next(): DBObject;

            /**
             * Returns as a query DBObject
             */
            getQuery(): DBObject;

            /**
             * Returns the length of the results
             */
            length(): number;

            /**
             * Sort the result by the orderBy parameter
             * @param orderBy
             */
            sort(orderBy);
            /**
             * Sets the results limit
             * @param orderBy
             */
            limit(limit);

            /**
             * Sets the min results
             * @param min
             */
            min(min);

            /**
             * Sets the max results
             * @param max
             */
            max(max);

            /**
             * Sets the maxTime timeout in ms
             * @param maxTime
             */
            maxTime(maxTime);

            /**
             * Gets the results size
             */
            size();

            /**
             * Skips the next numberOfElements
             * @param numberOfElements
             */
            skip(numberOfElements:number): number;
        }

        interface DBObject {
            /**
             * Adds a pair by key and value parameters
             * @param key
             * @param value
             */
            append(key, value): this;

            /**
             * Renders the DBObject as a JSON
             */
            toJson(): JSON;

            /**
             * Only matters if you are going to upsert and do not want to risk losing fields
             */
            isPartialObject(): boolean;


            containsField(key): boolean;

            get(key);

            /**
             * Adds the key-value pair
             * @param key
             * @param value
             */
            put(key, value);

            /**
             * Removes the field by key parameter
             * @param key
             */
            removeField(key);

            extract(dbObject: DBObject);

            implicit(object);
        }
    }

    module dao {
        /**
         * Creates new DAO instances from oConfiguraiton JS object, which can be either standard ORM definition or a standard dirigible table definition
         * @param oDefinition
         *@param loggerName
         */
        function create(oDefinition, loggerName?): DAO;

        interface DAO {
            /**
             * inserts array or entity and returns id (or ids of array of entities was supplied as input)
             * @param entity
             */
            insert(entity): any;

            /**
             * lists entities optionally constrained with the supplied query settings
             * @param oQuerySettings
             */
            list(oQuerySettings?): [];

            /**
             * returns an entity by its id(if any), optionally expanding inline the associations defined in expand and optionally constraining the entitiy properties to those specified in select
             * @param id
             * @param expand
             * @param select
             */
            find(id, expand?, select?): Object;

            /**
             * updates a persistent entity and returns for its dao chaining
             * @param entity
             */
            update(entity): DAO;

            remove(id: string);

            remove();

            /**
             * delete entity by id, or array of ids, or delete all (if not argument is provided).
             * @param id
             */
            remove(id: string[]);

            /**
             * returns the number of persisted entities
             */
            count(): number;

            /**
             * Kept for compatibility
             */
            createTable();

            /**
             * Kept for compatibility
             */
            dropTable();
        }
    }
}
