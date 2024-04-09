
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = '1,2,3,4,5,6';
var result = escape.escapeCsv(input);

assertEquals(result, '"1,2,3,4,5,6"');
