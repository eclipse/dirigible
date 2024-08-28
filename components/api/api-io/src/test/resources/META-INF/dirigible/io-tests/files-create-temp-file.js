
import { Files } from 'sdk/io/files';
const assertTrue = require('test/assert').assertTrue;

const tempFile = Files.createTempFile("dirigible", ".txt");

console.log('Temp file: ' + tempFile);

Files.writeText(tempFile, "Eclipse Dirigible");
Files.deleteFile(tempFile);

assertTrue((tempFile !== null) && (tempFile !== undefined));
