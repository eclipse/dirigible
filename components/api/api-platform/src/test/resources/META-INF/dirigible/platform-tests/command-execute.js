import { Command as command } from 'sdk/platform/command';
import { Assert } from 'test/assert';
import { OS as os } from 'sdk/platform/os';

const cmdForExec = os.isWindows() ? "cmd /c echo 'hello dirigible!'" : "echo 'hello dirigible!'";
const result = command.execute(cmdForExec);
console.log("[Result]: " + result);

Assert.assertTrue(result !== undefined && result !== null);
