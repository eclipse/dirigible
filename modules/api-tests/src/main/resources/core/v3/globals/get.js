/* eslint-env node, dirigible */

var globals = require('core/v3/globals');

globals.set("name1", "value1");
var result = globals.get('name1');

console.log(result);

result === 'value1';