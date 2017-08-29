/* eslint-env node, dirigible */

var hex = require('utils/v3/hex');

var input = '414243';
var result = hex.decode(input);

console.log('decoded: ' + result);

(result[0] === 65 &&
result[1] === 66 &&
result[2] === 67)
