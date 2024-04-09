
var request = require('http/request');
var assertEquals = require('test/assert').assertEquals;

assertEquals(request.getAuthType(), 'Basic');
