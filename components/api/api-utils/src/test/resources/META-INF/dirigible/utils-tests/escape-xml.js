
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = '"<>';
var result = escape.escapeXml(input);

assertEquals(result, '&quot;&lt;&gt;');
