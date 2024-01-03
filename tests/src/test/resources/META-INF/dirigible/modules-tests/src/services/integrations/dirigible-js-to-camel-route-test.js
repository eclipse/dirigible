import { test, assertEquals } from "@dirigible/junit"
import * as camel from "@dirigible/integrations"

test('dirigible-js-to-camel-route-test', () => {
    const message = "Initial Message";
    const expected = message + " -> camel route inbound1 handled this message";
	const actual = camel.invokeRoute('direct:inbound1', message, []);
    console.log('[CamelTest] Camel route inbound1 responded with message: ' + actual);
	assertEquals("Received an unexpected message from route inbound1 ", expected, actual);
});
