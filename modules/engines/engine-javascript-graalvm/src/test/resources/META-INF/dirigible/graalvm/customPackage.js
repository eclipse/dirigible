var helium = require("graalvm/helium");
var isInert = JSON.stringify(helium.isInert());
console.info(isInert);

isInert === "true"
