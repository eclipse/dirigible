
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = 'json \t characters \n';
var result = escape.escapeJavascript(input);

assertEquals(result, 'json \\t characters \\n');
