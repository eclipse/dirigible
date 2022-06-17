declare module "@dirigible/spark" {
    module client {
        interface Session {
            readDefault(oath): DatasetRow;

            readFormat(path, format): DatasetRow;
        }

        interface DatasetRow {
            getHeadRow(): Row;

            getRowAsString(rowNum): string;

            filterDataset(condition): DatasetRow;
        }

        interface Row {
            asJson(): JSON;
        }

        function getSession(sparkUri): Session;

        function getDBTableDataset(sparkUri, dbName, user, pass, table): DatasetRow;

    }
}