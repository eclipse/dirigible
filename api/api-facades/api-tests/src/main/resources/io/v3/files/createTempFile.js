/* eslint-env node, dirigible */

var files = require('io/v3/files');

var tempFile = files.createTempFile("dirigible", ".txt");
console.log('>>>>>>>>>> ' + tempFile);
files.writeText(tempFile, "Eclipse Dirigible");
files.deleteFile(tempFile);

((tempFile !== null) && (tempFile !== undefined));
