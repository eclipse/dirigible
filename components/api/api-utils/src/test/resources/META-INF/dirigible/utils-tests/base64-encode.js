
var base64 = require('utils/base64');
var assertEquals = require('test/assert').assertEquals;

var input = [61, 62, 63];
var result = base64.encode(input);

assertEquals(result, 'PT4/');
