import { Controller, Get, Post, Put, Delete, response } from "sdk/http";
import { query } from "sdk/db";

@Controller
class CRUDService {

    @Get("/")
    async getAll(req, res) {
        const { schemaName, tableName } = req.body;

        const sqlQuery = `
        SELECT * FROM "${schemaName}"."${tableName}";
    `;

        try {
            const result = query.execute(sqlQuery);
            res.send(result);
        } catch (error) {
            res.status(500).send({ error: "Failed to fetch rows", details: error });
        }
    }

    @Post("/create")
    async create(req, res) {
        const { schemaName, tableName, data } = req.body;

        if (!entityName || !data) {
            return res.status(400).send({ error: "Entity name and data are required" });
        }

        const columns = Object.keys(data);
        const values = Object.values(data);
        const placeholders = columns.map(() => "?");

        const sqlQuery = `
            INSERT INTO "${schemaName}"."${tableName}" 
            (${columns.join(", ")})
            VALUES (${placeholders.join(", ")});
        `;

        try {
            query.execute(sqlQuery, values);
            res.send({ success: true });
        } catch (error) {
            res.status(500).send({ error: "Failed to create row", details: error });
        }
    }

    @Put("/update")
    async update(req, res) {
        const { tableName, schemaName, data, primaryKey } = req.body;
        debugger
        if (!entityName || !data || !primaryKey) {
            return res.status(400).send({ error: "Entity name, data, and primary key are required" });
        }

        const setClauses = [];
        const parameters = [];
        const whereClauses = [];

        Object.keys(data).forEach((key) => {
            if (!primaryKey.includes(key)) {
                setClauses.push(`"${key}" = ?`);
                parameters.push(data[key]);
            } else {
                whereClauses.push(`"${key}" = ?`);
                parameters.push(data[key]);
            }
        });

        const sqlQuery = `
            UPDATE "${schemaName}"."${tableName}"
            SET ${setClauses.join(", ")}
            WHERE ${whereClauses.join(" AND ")};
        `;

        try {
            query.execute(sqlQuery, parameters);
            res.send({ success: true });
        } catch (error) {
            res.status(500).send({ error: "Failed to update row", details: error });
        }
    }

    @Delete("/delete")
    async delete(req, res) {
        const { schemaName, tableName, primaryKey } = req.body;

        if (!entityName || !primaryKey) {
            return res.status(400).send({ error: "Entity name and primary key are required" });
        }

        const whereClauses = [];
        const parameters = [];

        primaryKey.forEach((key) => {
            whereClauses.push(`"${key}" = ?`);
            parameters.push(req.body[key]);
        });

        const sqlQuery = `
            DELETE FROM "${schemaName}"."${tableName}"
            WHERE ${whereClauses.join(" AND ")};
        `;

        try {
            query.execute(sqlQuery, parameters);
            res.send({ success: true });
        } catch (error) {
            res.status(500).send({ error: "Failed to delete row", details: error });
        }
    }
}
