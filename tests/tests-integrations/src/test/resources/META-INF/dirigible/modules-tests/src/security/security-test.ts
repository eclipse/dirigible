import { user } from "sdk/security";
import { assert } from "sdk/test";

test('get-user-test', () => {
	assert.assertEquals(user.getName(), 'guest');
});
