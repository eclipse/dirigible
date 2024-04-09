
var response = require('http/response');
var assertTrue = require('test/assert').assertTrue;

assertTrue(response.getHeaderNames().includes("header1","header2"));

