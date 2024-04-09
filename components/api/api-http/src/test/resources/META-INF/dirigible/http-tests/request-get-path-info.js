
var request = require('http/request');
var assertEquals = require('test/assert').assertEquals;

assertEquals(request.getPathInfo(), '/services/js/http-tests/request-get-path-info.js');
