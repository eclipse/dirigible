declare module "@dirigible/qldb" {
    module QLDBRepository {
        /**
         *  Constructs a repository with given ledger and table.
         *  Notice: If the table does not exist you must call .createTable()
         */
        constructor(ledgerName: string, tableName: string);

        /**
         *  Creates the repository table.
         */
        function createTable();

        /**
         *  Drops the repository table.
         *  Notice: Dropping tables in QLDB inactivates them.
         *  You can undo that with an UNDROP statement in PartiQL.
         */
        function dropTable();

        /**
         *  Inserts the entry in the repository.
         *  On success returns the inserted entry.
         */
        function insert(entry: object): object;

        /**
         *  Updates the entry in the repository.
         *  On success returns the updated entry.
         *  Notice: The entry must contain a documentId property.
         */
        function update(entry: object): object;

        /**
         *  Returns the repository entry with given documentId.
         */
        function getById(documentId: string): object;

        /**
         *  Returns all the repository entries.
         */
        function getAll(): object[];

        /**
         *  Deletes the entry by given documentId.documentId.
         *  On success returns the documentId of the deleted object.
         */
        function delete(documentId: string): string;

        /**
         *  Deletes the entry.
         *  On success returns the documentId of the deleted object.
         *  Notice: The entry must contain a documentId property.
         */
        function delete(entry: object): string;

        /**
         *  Returns the entire history of transactions of the repository.
         */
        function getHistory(): object[];

        /**
         *  Returns the ledger name the repository is working against.
         */
        function getLedgerName(): string;

        /**
         *  Returns the table name the repository is working against.
         */
        function getTableName(): string;
    }
}
