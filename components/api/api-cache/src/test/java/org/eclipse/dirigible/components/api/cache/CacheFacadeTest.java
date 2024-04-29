package org.eclipse.dirigible.components.api.cache;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CacheFacadeTest {

    private static final String KEY = "key1";
    private static final String VALUE = "value1";

    @Test
    void testCache() {
        assertMissingEntry();

        CacheFacade.set(KEY, VALUE);
        assertThat(CacheFacade.contains(KEY)).isTrue();
        assertThat(CacheFacade.get(KEY)).isEqualTo(VALUE);

        CacheFacade.delete(KEY);
        assertMissingEntry();

        CacheFacade.set(KEY, VALUE);
        CacheFacade.clear();
        assertMissingEntry();

    }

    private static void assertMissingEntry() {
        assertThat(CacheFacade.contains(KEY)).isFalse();
        assertThat(CacheFacade.get(KEY)).isNull();
    }
}
