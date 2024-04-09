
var files = require('io/files');
var assertTrue = require('test/assert').assertTrue;

var tempFile = files.createTempFile("dirigible", ".txt");
console.log('Temp file: ' + tempFile);
files.writeText(tempFile, "Eclipse Dirigible");
files.deleteFile(tempFile);

assertTrue((tempFile !== null) && (tempFile !== undefined));
