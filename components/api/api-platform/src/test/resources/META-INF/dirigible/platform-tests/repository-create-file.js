
var repository = require("platform/repository");
var assertTrue = require('test/assert').assertTrue;

repository.createResource("/registry/public/test/file.js", "console.log('Hello World');", "application/json");
var resource = repository.getResource("/registry/public/test/file.js");
var content = resource.getText();

assertTrue(content !== undefined && content !== null);