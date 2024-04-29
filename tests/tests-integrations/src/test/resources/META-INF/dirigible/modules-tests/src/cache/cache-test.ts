import { test, assertEquals, assertTrue, assertFalse } from "sdk/junit"
import { caches } from "sdk/cache";

test('cache-api-test', () => {

    const KEY = "key1";
    const VALUE = "value1";

    caches.clear();

    assertFalse(caches.contains(KEY))
    assertEquals(null, caches.get(KEY));

    caches.set(KEY, VALUE);
    assertTrue(caches.contains(KEY))
    assertEquals(VALUE, caches.get(KEY));

    caches.delete(KEY);
    assertFalse(caches.contains(KEY))
    assertEquals(null, caches.get(KEY));

    caches.set(KEY, VALUE);
    caches.clear();

    assertFalse(caches.contains(KEY))
    assertEquals(null, caches.get(KEY));
});
