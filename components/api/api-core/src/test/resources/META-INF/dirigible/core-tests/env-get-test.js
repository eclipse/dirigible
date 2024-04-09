
var env = require('core/env');
var assertTrue = require('test/assert').assertTrue;

var obj = env.list();
var key = Object.keys(obj)[0];

var result = env.get(key);

assertTrue(result !== undefined && result !== null);
