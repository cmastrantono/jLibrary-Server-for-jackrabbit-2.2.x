/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.core.util;

/*
 * $Id: UtilCache.java,v 1.1 2009/12/09 18:41:06 ekoleszar Exp $
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.*;



/**
 * <p> Generalized caching utility. Provides a number of caching features:
 *   <ul>
 *    <li>Limited or unlimited element capacity
 *    <li>If limited, removes elements with the LRU (Least Recently Used) algorithm
 *    <li>Keeps track of when each element was loaded into the cache
 *    <li>Using the expireTime can report whether a given element has expired
 *    <li>Counts misses and hits
 *   </ul>
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 16, 2001
 *@version    1.0
 */
public class UtilCache {

    /** A static Map to keep track of all of the UtilCache instances. */
    public static Map utilCacheTable = new HashMap();

    /** An index number appended to utilCacheTable names when there are conflicts. */
    protected static Map defaultIndices = new HashMap();

    /** The name of the UtilCache instance, is also the key for the instance in utilCacheTable. */
    protected String name;

    /** A list of the elements order by Least Recent Use */
    public LinkedList keyLRUList = new LinkedList();

    /** A hashtable containing a CacheLine object with a value and a loadTime for each element. */
    public Map cacheLineTable = new HashMap();

    /** A count of the number of cache hits */
    protected long hitCount = 0;

    /** A count of the number of cache misses */
    protected long missCount = 0;

    /** The maximum number of elements in the cache.
     * If set to 0, there will be no limit on the number of elements in the cache.
     */
    protected long maxSize = 0;

    /** Specifies the amount of time since initial loading before an element will be reported as expired.
     * If set to 0, elements will never expire.
     */
    protected long expireTime = 0;

    /** Specifies whether or not to use soft references for this cache, defaults to false */
    protected boolean useSoftReference = false;

    /** Constructor which specifies the cacheName as well as the maxSize, expireTime and useSoftReference.
     * The passed maxSize, expireTime and useSoftReference will be overridden by values from cache.properties if found.
     * @param maxSize The maxSize member is set to this value
     * @param expireTime The expireTime member is set to this value
     * @param cacheName The name of the cache.
     * @param useSoftReference Specifies whether or not to use soft references for this cache.
     */
    public UtilCache(String cacheName, long maxSize, long expireTime, boolean useSoftReference) {
        this.useSoftReference = useSoftReference;
        this.maxSize = maxSize;
        this.expireTime = expireTime;
        setPropertiesParams(cacheName);

        name = cacheName + this.getNextDefaultIndex(cacheName);
        utilCacheTable.put(name, this);
    }

    /** Constructor which specifies the cacheName as well as the maxSize and expireTime.
     * The passed maxSize and expireTime will be overridden by values from cache.properties if found.
     * @param maxSize The maxSize member is set to this value
     * @param expireTime The expireTime member is set to this value
     * @param cacheName The name of the cache.
     */
    public UtilCache(String cacheName, long maxSize, long expireTime) {
        this(cacheName, maxSize, expireTime, false);
    }

    /** Constructor which specifies the maxSize and expireTime.
     * @param maxSize The maxSize member is set to this value
     * @param expireTime The expireTime member is set to this value
     */
    public UtilCache(long maxSize, long expireTime) {
        this.useSoftReference = false;
        this.maxSize = maxSize;
        this.expireTime = expireTime;
        String name = "specified" + this.getNextDefaultIndex("specified");

        setPropertiesParams(name);
        utilCacheTable.put(name, this);
    }

    /** This constructor takes a name for the cache, puts itself in the utilCacheTable.
     * It also uses the cacheName to lookup the initialization parameters from cache.properties.
     * @param cacheName The name of the cache.
     */
    public UtilCache(String cacheName) {
        setPropertiesParams("default");
        setPropertiesParams(cacheName);

        name = cacheName + this.getNextDefaultIndex(cacheName);
        utilCacheTable.put(name, this);
    }

    /** Default constructor, all members stay at default values as defined in cache.properties, or the defaults in this file if cache.properties is not found, or there are no 'default' entries in it. */
    public UtilCache() {
        setPropertiesParams("default");

        name = "default" + this.getNextDefaultIndex("default");
        utilCacheTable.put(name, this);
    }

    protected String getNextDefaultIndex(String cacheName) {
        Integer curInd = (Integer) defaultIndices.get(cacheName);

        if (curInd == null) {
            defaultIndices.put(cacheName, new Integer(1));
            return "";
        } else {
            defaultIndices.put(cacheName, new Integer(curInd.intValue() + 1));
            return Integer.toString(curInd.intValue() + 1);
        }
    }

    protected void setPropertiesParams(String cacheName) {
        ResourceBundle res = ResourceBundle.getBundle("cache");

        if (res != null) {
            try {
                String value = res.getString(cacheName + ".maxSize");
                Long longValue = new Long(value);

                if (longValue != null) {
                    maxSize = longValue.longValue();
                }
            } catch (Exception e) {}
            try {
                String value = res.getString(cacheName + ".expireTime");
                Long longValue = new Long(value);

                if (longValue != null) {
                    expireTime = longValue.longValue();
                }
            } catch (Exception e) {}

            try {
                String value = res.getString(cacheName + ".useSoftReference");

                if (value != null) {
                    useSoftReference = "true".equals(value);
                }
            } catch (Exception e) {}
        }
    }

    /** Puts or loads the passed element into the cache
     * @param key The key for the element, used to reference it in the hastables and LRU linked list
     * @param value The value of the element
     */
    public synchronized void put(Object key, Object value) {
        if (key == null)
            return;

        if (maxSize > 0) {
            // when maxSize is changed, the setter will take care of filling the LRU list
            if (cacheLineTable.containsKey(key)) {
                keyLRUList.remove(key);
                keyLRUList.addFirst(key);
            } else {
                keyLRUList.addFirst(key);
            }
        }

        if (expireTime > 0) {
            cacheLineTable.put(key, new UtilCache.CacheLine(value, useSoftReference, System.currentTimeMillis()));
        } else {
            cacheLineTable.put(key, new UtilCache.CacheLine(value, useSoftReference));
        }
        if (maxSize > 0 && cacheLineTable.size() > maxSize) {
            Object lastKey = keyLRUList.getLast();

            remove(lastKey);
        }
    }

    /** Gets an element from the cache according to the specified key.
     * If the requested element hasExpired, it is removed before it is looked up which causes the function to return null.
     * @param key The key for the element, used to reference it in the hastables and LRU linked list
     * @return The value of the element specified by the key
     */
    public Object get(Object key) {
        if (key == null) {
            missCount++;
            return null;
        }
        UtilCache.CacheLine line = (UtilCache.CacheLine) cacheLineTable.get(key);

        if (hasExpired(line)) {
            // note that print.info in debug.properties cannot be checked through UtilProperties here, it would cause infinite recursion...
            // if (Debug.infoOn()) Debug.logInfo("Element has expired with key " + key);
            remove(key);
            line = null;
        }

        if (line == null) {
            // if (Debug.infoOn()) Debug.logInfo("Element not found with key " + key);
            missCount++;
            return null;
        }
        // if (Debug.infoOn()) Debug.logInfo("Element found with key " + key);
        hitCount++;

        if (maxSize > 0) {
            keyLRUList.remove(key);
            keyLRUList.addFirst(key);
        }
        return line.getValue();
    }

    /** Removes an element from the cache according to the specified key
     * @param key The key for the element, used to reference it in the hastables and LRU linked list
     * @return The value of the removed element specified by the key
     */
    public synchronized Object remove(Object key) {
        if (key != null && cacheLineTable.containsKey(key)) {
            UtilCache.CacheLine line = (UtilCache.CacheLine) cacheLineTable.get(key);

            cacheLineTable.remove(key);
            keyLRUList.remove(key);
            return line.getValue();
        } else {
            missCount++;
            return null;
        }
    }

    /** Removes all elements from this cache */
    public synchronized void clear() {
        cacheLineTable.clear();
        keyLRUList.clear();
        clearCounters();
    }

    /** Removes all elements from this cache */
    public static void clearAllCaches() {
        Iterator entries = utilCacheTable.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            UtilCache utilCache = (UtilCache) entry.getValue();
            utilCache.clear();
        }
    }

    /** Getter for the name of the UtilCache instance.
     * @return The name of the instance
     */
    public String getName() {
        return name;
    }

    /** Returns the number of successful hits on the cache
     * @return The number of successful cache hits
     */
    public long getHitCount() {
        return hitCount;
    }

    /** Returns the number of cache misses
     * @return The number of cache misses
     */
    public long getMissCount() {
        return missCount;
    }

    /** Clears the hit and miss counters
     */
    public void clearCounters() {
        hitCount = 0;
        missCount = 0;
    }

    /** Sets the maximum number of elements in the cache.
     * If 0, there is no maximum.
     * @param maxSize The maximum number of elements in the cache
     */
    public void setMaxSize(long maxSize) {
        // if the new maxSize is <= 0, clear keyLRUList
        if (maxSize <= 0) {
            keyLRUList.clear();
        } else if (maxSize > 0 && this.maxSize <= 0) {
            // if the new maxSize > 0 and the old is <= 0, fill in LRU list - order will be meaningless for now
            Iterator keys = cacheLineTable.keySet().iterator();

            while (keys.hasNext()) {
                keyLRUList.add(keys.next());
            }
        }

        // if the new maxSize is less than the current cache size, shrink the cache.
        if (maxSize > 0 && cacheLineTable.size() > maxSize) {
            while (cacheLineTable.size() > maxSize) {
                Object lastKey = keyLRUList.getLast();

                remove(lastKey);
            }
        }

        this.maxSize = maxSize;
    }

    /** Returns the current maximum number of elements in the cache
     * @return The maximum number of elements in the cache
     */
    public long getMaxSize() {
        return maxSize;
    }

    /** Sets the expire time for the cache elements.
     * If 0, elements never expire.
     * @param expireTime The expire time for the cache elements
     */
    public void setExpireTime(long expireTime) {
        // if expire time was <= 0 and is now greater, fill expire table now
        if (this.expireTime <= 0 && expireTime > 0) {
            long currentTime = System.currentTimeMillis();
            Iterator values = cacheLineTable.values().iterator();

            while (values.hasNext()) {
                UtilCache.CacheLine line = (UtilCache.CacheLine) values.next();

                line.loadTime = currentTime;
            }
        } else if (this.expireTime <= 0 && expireTime > 0) {// if expire time was > 0 and is now <=, do nothing, just leave the load times in place, won't hurt anything...
        }

        this.expireTime = expireTime;
    }

    /** return the current expire time for the cache elements
     * @return The expire time for the cache elements
     */
    public long getExpireTime() {
        return expireTime;
    }

    /** Set whether or not the cache lines should use a soft reference to the data */
    public void setUseSoftReference(boolean useSoftReference) {
        if (this.useSoftReference != useSoftReference) {
            this.useSoftReference = useSoftReference;
            Iterator values = cacheLineTable.values().iterator();

            while (values.hasNext()) {
                UtilCache.CacheLine line = (UtilCache.CacheLine) values.next();

                line.setUseSoftReference(useSoftReference);
            }
        }
    }

    /** Return whether or not the cache lines should use a soft reference to the data */
    public boolean getUseSoftReference() {
        return this.useSoftReference;
    }

    /** Returns the number of elements currently in the cache
     * @return The number of elements currently in the cache
     */
    public long size() {
        return cacheLineTable.size();
    }

    /** Returns a boolean specifying whether or not an element with the specified key is in the cache.
     * If the requested element hasExpired, it is removed before it is looked up which causes the function to return false.
     * @param key The key for the element, used to reference it in the hastables and LRU linked list
     * @return True is the cache contains an element corresponding to the specified key, otherwise false
     */
    public boolean containsKey(Object key) {
        UtilCache.CacheLine line = (UtilCache.CacheLine) cacheLineTable.get(key);

        if (hasExpired(line)) {
            remove(key);
            line = null;
        }
        if (line != null) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns a boolean specifying whether or not the element corresponding to the key has expired.
     * Only returns true if element is in cache and has expired. Error conditions return false, if no expireTable entry, returns true.
     * Always returns false if expireTime <= 0.
     * Also, if SoftReference in the CacheLine object has been cleared by the gc return true.
     *
     * @param key The key for the element, used to reference it in the hastables and LRU linked list
     * @return True is the element corresponding to the specified key has expired, otherwise false
     */
    public boolean hasExpired(Object key) {
        if (key == null) return false;

        UtilCache.CacheLine line = (UtilCache.CacheLine) cacheLineTable.get(key);

        return hasExpired(line);
    }

    protected boolean hasExpired(UtilCache.CacheLine line) {
        if (line == null) return false;
        // check this BEFORE checking to see if expireTime <= 0, ie if time expiration is enabled
        // check to see if we are using softReference first, slight performance increase
        if (this.useSoftReference && line.getValue() == null) return true;
        if (expireTime <= 0) return false;

        if (line.loadTime <= 0) return true;
        if ((line.loadTime + expireTime) < System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

    /** Clears all expired cache entries; also clear any cache entries where the SoftReference in the CacheLine object has been cleared by the gc */
    public void clearExpired() {
        Iterator keys = cacheLineTable.keySet().iterator();

        while (keys.hasNext()) {
            Object key = keys.next();

            if (hasExpired(key)) {
                remove(key);
            }
        }
    }

    /** Clears all expired cache entries from all caches */
    public static void clearExpiredFromAllCaches() {
        Iterator entries = utilCacheTable.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            UtilCache utilCache = (UtilCache) entry.getValue();

            utilCache.clearExpired();
        }
    }

    public static class CacheLine {
        public Object valueRef = null;
        public long loadTime = 0;
        public boolean useSoftReference = false;

        public CacheLine(Object value, boolean useSoftReference) {
            if (useSoftReference) {
                this.valueRef = new java.lang.ref.SoftReference(value);
            } else {
                this.valueRef = value;
            }
            this.useSoftReference = useSoftReference;
        }

        public CacheLine(Object value, boolean useSoftReference, long loadTime) {
            this(value, useSoftReference);
            this.loadTime = loadTime;
        }

        public Object getValue() {
            if (valueRef == null) return null;
            if (useSoftReference) {
                return ((java.lang.ref.SoftReference) valueRef).get();
            } else {
                return valueRef;
            }
        }

        public void setUseSoftReference(boolean useSoftReference) {
            if (this.useSoftReference != useSoftReference) {
                synchronized (this) {
                    this.useSoftReference = useSoftReference;
                    if (useSoftReference) {
                        this.valueRef = new java.lang.ref.SoftReference(this.valueRef);
                    } else {
                        this.valueRef = ((java.lang.ref.SoftReference) this.valueRef).get();
                    }
                }
            }
        }
    }
}
