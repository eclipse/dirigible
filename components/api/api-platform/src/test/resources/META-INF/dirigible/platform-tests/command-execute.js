
const command = require('platform/command');
const assertTrue = require('test/assert').assertTrue;
const os = require('platform/os');

const cmdForExec = os.isWindows() ? "cmd /c echo 'hello dirigible!'" : "echo 'hello dirigible!'";
var result = command.execute(cmdForExec);
console.log("[Result]: " + result);

assertTrue(result !== undefined && result !== null);
