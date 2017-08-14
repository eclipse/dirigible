/* eslint-env node, dirigible */

var globals = require('core/v3/globals');

var result = globals.list();

console.log(JSON.stringify(result));

result !== undefined && result !== null;
