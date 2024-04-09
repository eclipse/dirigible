
var session = require('http/session');
var assertEquals = require('test/assert').assertEquals;

session.setAttribute('attr1', 'value1');

assertEquals(JSON.stringify(session.getAttributeNames()), '["invocation.count","attr1"]');

