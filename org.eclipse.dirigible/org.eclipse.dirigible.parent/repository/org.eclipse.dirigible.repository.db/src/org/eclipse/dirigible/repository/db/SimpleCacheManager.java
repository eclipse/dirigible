/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.db;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.dirigible.repository.logging.Logger;

public class SimpleCacheManager {
	
	private static final Logger logger = Logger.getLogger(SimpleCacheManager.class);
	
	private static final long MAX_EXPIRATION_TIME = 10000; 

    private Map<String, ExpirationWrapper> cache = Collections.synchronizedMap(new WeakHashMap<String, ExpirationWrapper>());
    
    private boolean disabled = true;

    public SimpleCacheManager(boolean disableCache) {
    	this.disabled = disableCache;
    }

    public void put(String cacheKey, Object value) {
    	if (!isDisabled() && value != null && (cache.get(cacheKey) == null)) {
    		logger.debug("put: " + cacheKey + " value: " + value);
	    	ExpirationWrapper wrapper = new ExpirationWrapper(value, GregorianCalendar.getInstance().getTime().getTime());
	        cache.put(cacheKey, wrapper);
    	}
    }
    
    public boolean isDisabled() {
		return disabled;
	}
    
    public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

    public Object get(String cacheKey) {
    	ExpirationWrapper wrapper = cache.get(cacheKey);
    	if (wrapper != null) {
	    	if ((GregorianCalendar.getInstance().getTime().getTime() - wrapper.getCreated()) < MAX_EXPIRATION_TIME) {
	    		logger.debug("cache used for: " + cacheKey);
	    		return wrapper.value;
	    	} else {
	    		logger.debug("cache expired for: " + cacheKey);
	    		clear(cacheKey);
	    	}
    	}
        return null;
    }

    public void clear(String cacheKey) {
        cache.remove(cacheKey);
    }

    public void clear() {
        cache.clear();
    }

//    public static SimpleCacheManager getInstance(boolean disableCache) {
//        if (instance == null) {
//            synchronized (monitor) {
//                if (instance == null) {
//                    instance = new SimpleCacheManager(disableCache);
//                }
//            }
//        }
//        return instance;
//    }
    
    class ExpirationWrapper {
    	
    	private Object value; 
    	private long created;
    	
    	ExpirationWrapper(Object value, long created) {
    		this.value = value;
    		this.created = created;
    	}
    	
    	public Object getValue() {
			return value;
		}
    	
    	public long getCreated() {
			return created;
		}
    	
    	
    }

}