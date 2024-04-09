
var cmis = require('cms/cmis');
var assertTrue = require('test/assert').assertTrue;

var result = cmis.getSession();

assertTrue(result !== null && result !== undefined);
