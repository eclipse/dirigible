// @ts-ignore
class byte {}
declare module "@dirigible/documents" {
    module pdf {
        /**
         * Generates PDF file from a given template and data
         * @param templatePath
         * @param data
         */
        function generate(templatePath: string, data): byte[];

        /**
         * Generates PDF file with table data and config
         * @param data
         * @param config
         */
        function generateTable(data: TableData, config?:TableConfig): byte[];

        interface TableConfig{
            /**
             * (Optional) The PDF document size.
             */
            size?:string;
            /**
             * (Optional) The column alignment property.Should be 'start' or 'center' or 'end'.
             */
            alignColumns?:string;
            /**
             * (Optional) The rows alignment property.Should be 'start' or 'center' or 'end'.
             */
            alignRows:string;
        }

        interface TableDataColumn {
            /**
             * The displayed column name.
             */
            name: string;
            /**
             * The property key in the rows object.
             */
            key: string;
        }

        interface TableDataRow {
            /**
             * The row data.
             */
            data: any;
            /**
             * (Optional) Whether to highlight the row.
             */
            highlight: boolean;
            /**
             *(Optional) Whether to bold the row.
             */
            breakAfter: boolean;
        }

        interface TableData {
            /**
             * (Optional) Title of the PDF document.
             */
            title?: string;
            /**
             * (Optional) Description of the PDF document.
             */
            description: string;
            /**
             * The table columns.
             */
            columns: TableDataColumn[];
            /**
             * The table data.
             */
            rows: TableDataRow[];
        }
    }

}
