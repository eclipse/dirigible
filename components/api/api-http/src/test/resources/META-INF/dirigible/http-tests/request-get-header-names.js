
var request = require('http/request');
var assertEquals = require('test/assert').assertEquals;

assertEquals(JSON.stringify(request.getHeaderNames()), '["Authorization","header1","header2"]');

