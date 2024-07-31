import { user } from "sdk/security";
import { assertEquals } from "sdk/junit"

test('get-user-test', () => {
	assertEquals('Unexpected user', user.getName(), 'guest');
});
