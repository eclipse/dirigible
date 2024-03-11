package org.eclipse.dirigible.components.api.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.TimeUnit;

public class CacheFacade {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);
    private static Cache<String, String> cache;

    /**
     * Instantiates a new caffeine database metadata cache.
     */
    public CacheFacade() {
        initCache();
    }

    /**
     * Inits the cache.
     */
    private void initCache() {
        cache = Caffeine.newBuilder()
                .build();
    }

        /**
         * Getter for the value of the property by its key.
         *
         * @param key the key
         * @return the string
         */
    public static String get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * Set.
     *
     * @param key the path
     * @param content the content
     */
    public void set(String key, String content) {
        if (content != null) {
            cache.put(key, content);
        }
    }

    /**
     * Deletes the instance.
     *
     * @param key the path
     */
    public void delete(String key) {
        cache.invalidate(key);
    }

    /**
     * Clear.
     */
    public void clear() {
        cache.invalidateAll();
    }
}
