
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = 'javascript \t characters \n';
var result = escape.escapeJavascript(input);

assertEquals(result, 'javascript \\t characters \\n');
