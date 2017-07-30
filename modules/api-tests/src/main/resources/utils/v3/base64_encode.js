/* eslint-env node, dirigible */

var base64 = require('utils/v3/base64');

var input = [61, 62, 63];
var result = base64.encode(input);

result === 'PT4/';
