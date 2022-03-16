var helium = require("graalvm/helium");
var assertTrue = require('utils/assert').assertTrue;
var isInert = JSON.stringify(helium.isInert());
console.info(isInert);

assertTrue(isInert === "true");
