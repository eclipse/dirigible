/* eslint-env node, dirigible */

var database = require('db/v3/database');

console.log(JSON.stringify(database.getDatabaseTypes()));

database.getDatabaseTypes() === '["derby"]';