
var request = require('http/request');
var assertEquals = require('test/assert').assertEquals;

assertEquals(request.getAttribute('attr1'), 'val1');
