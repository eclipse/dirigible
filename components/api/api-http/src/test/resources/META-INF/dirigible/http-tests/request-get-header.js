
var request = require('http/request');
var assertEquals = require('test/assert').assertEquals;

assertEquals(request.getHeader('header1'), 'header1');
