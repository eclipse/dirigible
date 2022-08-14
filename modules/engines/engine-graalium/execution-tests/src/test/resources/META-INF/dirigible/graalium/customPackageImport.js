var helium = require("graalium/heliumImport");
var assertTrue = require('utils/assert').assertTrue;
var isInert = JSON.stringify(helium.isInert());
console.info(isInert);

assertTrue(isInert === "true");
