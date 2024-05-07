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

test('cache-api-boolean', () => {

    const KEY = "boolean-key";
    const VALUE = true;

    caches.clear();

    caches.set(KEY, VALUE);
    assertEquals(VALUE, caches.get(KEY));
});

test('cache-api-number', () => {

    const KEY = "number-key";
    const VALUE = 123;

    caches.clear();

    caches.set(KEY, VALUE);
    assertEquals(VALUE, caches.get(KEY));
});

test('cache-api-string', () => {

    const KEY = "string-key";
    const VALUE = "This is a string value";

    caches.clear();

    caches.set(KEY, VALUE);
    assertEquals(VALUE, caches.get(KEY));
});

function isEqual(obj1, obj2) {
    if (obj1 === null || obj1 === undefined || obj2 === null || obj2 === undefined) {
        return obj1 === obj2;
    }

    if (typeof obj1 !== 'object' || typeof obj2 !== 'object') {
        return obj1 === obj2;
    }

    if (Object.keys(obj1).length !== Object.keys(obj2).length) {
        return false;
    }

    for (let key in obj1) {
        if (!isEqual(obj1[key], obj2[key])) {
            return false;
        }
    }

    return true;
}

test('cache-api-object', () => {

    const KEY = "object-key";
    const VALUE = {
        name : "Ivan",
        age: 15,
        parent:{
          name : "Patar",
                age: 35
        }
    };

    caches.clear();

    caches.set(KEY, VALUE);
    assertTrue(isEqual(VALUE, caches.get(KEY));
});
