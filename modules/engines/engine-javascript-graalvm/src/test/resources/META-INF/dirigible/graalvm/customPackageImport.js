var helium = require("graalvm/heliumImport");
var isInert = JSON.stringify(helium.isInert());
console.info(isInert);

isInert === "true"
