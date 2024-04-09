
var engines = require('platform/engines');
var assertTrue = require('test/assert').assertTrue;

var result = engines.getTypes();

assertTrue(result !== undefined && result !== null);
