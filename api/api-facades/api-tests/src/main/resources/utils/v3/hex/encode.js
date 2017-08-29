/* eslint-env node, dirigible */

var hex = require('utils/v3/hex');

var input = [65, 66, 67];
var result = hex.encode(input);

result === '414243';
