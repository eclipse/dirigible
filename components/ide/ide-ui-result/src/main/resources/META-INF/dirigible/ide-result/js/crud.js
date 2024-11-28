import { query } from "sdk/db";

const CrudService = {
    executeQuery: function ({ sqlQuery, parameters = [] }) {
        if (!sqlQuery) {
            console.error("Query is required.");
            return Promise.reject("Query is required.");
        }

        let result = query.execute(sqlQuery, parameters);

        return result;
    }
};

export default CrudService;
