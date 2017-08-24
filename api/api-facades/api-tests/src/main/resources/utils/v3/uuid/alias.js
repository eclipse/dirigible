/* eslint-env node, dirigible */

var uuid = require('utils/uuid');

var generated = uuid.random();

console.log(generated);

uuid.validate(generated);


