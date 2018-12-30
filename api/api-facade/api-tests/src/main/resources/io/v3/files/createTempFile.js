var files = require('io/v3/files');

var tempFile = files.createTempFile("dirigible", ".txt");
console.log('Temp file: ' + tempFile);
files.writeText(tempFile, "Eclipse Dirigible");
files.deleteFile(tempFile);

((tempFile !== null) && (tempFile !== undefined));
