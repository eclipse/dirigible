import { response } from "@dirigible/http";
import { database } from "@dirigible/db";

let extensionPoints = [];

let connection = null;
try {
    connection = database.getConnection("local", "SystemDB");
    let statement = connection.prepareStatement("SELECT EXTENSIONPOINT_NAME FROM DIRIGIBLE_EXTENSION_POINTS");
    let resultSet = statement.executeQuery();
    while (resultSet.next()) {
        extensionPoints.push(resultSet.getString("EXTENSIONPOINT_NAME"));
    }
} finally {
    if (connection != null) {
        connection.close();
    }
}
response.setContentType("application/json");
response.println(JSON.stringify(extensionPoints));
response.flush();
response.close();