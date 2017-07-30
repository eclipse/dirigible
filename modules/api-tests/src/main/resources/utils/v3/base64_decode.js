/* eslint-env node, dirigible */

var base64 = require('utils/v3/base64');

var input = 'PT4/';
var result = base64.decode(input);

(result[0] === 61 &&
result[1] === 62 &&
result[2] === 63)
