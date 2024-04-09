
var hex = require('utils/hex');
var assertEquals = require('test/assert').assertEquals;

var input = [65, 66, 67];
var result = hex.encode(input);

assertEquals(result, '414243');
