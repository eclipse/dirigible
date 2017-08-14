/* eslint-env node, dirigible */

var env = require('core/v3/env');

var result = env.get('SHELL');

console.log(JSON.stringify(result));

result !== undefined && result !== null;
