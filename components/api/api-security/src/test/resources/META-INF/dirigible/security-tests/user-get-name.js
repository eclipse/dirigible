
import { user } from "sdk/security";
import { assert } from "sdk/test";
const assertEquals = assert.assertEquals;

assertEquals(user.getName(), 'guest');
