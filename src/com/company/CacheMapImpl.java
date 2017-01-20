package com.company;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by userf on 19.01.2017.
 */
public class CacheMapImpl<KeyType, ValueType> implements CacheMap<KeyType, ValueType>{
    private long ttl;
    private HashMap<KeyType, ValueType> store;
    private ArrayList<ExpiredNode> expired = new ArrayList<>();

    CacheMapImpl(){
        ttl = 1000;
        store = new HashMap<>();
//        System.out.println("CacheMapImpl inited");
    }

    public void setTimeToLive(long timeToLive){
        this.ttl = timeToLive;
    }

    public long getTimeToLive(){
        return this.ttl;
    }

    /**
     * Caches the given value under the given key.
     *
     * If there already is an item under the given key, it will be replaced by the new value. <p>
     *
     * @param key may not be null
     * @param item may be null, in which case the cache entry will be removed (if it existed).
     * @return the previous value, or null if none
     */
    public ValueType put(KeyType key, ValueType value){
        ValueType result = store.put(key,value);
        if (ttl>0){
            if (result!=null){
//              Predicate<ExpiredNode> node = n-> n.getKey() == key;
                expired.removeIf((ExpiredNode n)-> n.getKey() == key);
                store.remove(key);
            }
            expired.add(new ExpiredNode(key, ttl));
        }
        return result;
    }

    /**
     * Clears all expired entries.
     * This is called automatically in conjuction with most operations,
     * but for memory optimization reasons you may call this explicitely at any time.
     */
    public void clearExpired(){
        long curtime = Clock.getTime();
        Iterator<ExpiredNode> iter = expired.iterator();
//        System.out.println("");
//        System.out.println("Clearing...");
        int count = 0;
        while (iter.hasNext()) {
            ExpiredNode n = iter.next();
            if (n.getExpired()<=curtime) {
//                System.out.println("... "+store.get(n.getKey())+" removed");
                store.remove(n.getKey());
                iter.remove();
                count++;
            }
        }
//        System.out.println("Result: "+count+" items cleared");
//        System.out.println("");
    }

    /**
     * Removes all entries.
     */
    public void clear(){
        store.clear();
        expired.clear();
    }

    /**
     * Checks if the given key is included in this cache map.
     */
    public boolean containsKey(Object key){
        clearExpired();
        return store.containsKey(key);
    }

    /**
     * Checks if the given value is included in this cache map.
     */
    public boolean containsValue(Object value){
        clearExpired();
        return store.containsValue(value);
    }

    /**
     * Returns the value for the given key. Null if there is no value,
     * or if it has expired.
     */
    public ValueType get(Object key){
        clearExpired();
        return store.get(key);
    }

    /**
     * True if this cache is empty.
     */
    public boolean isEmpty(){
        this.clearExpired();
        return store.isEmpty();
    }

    /**
     * Removes the given key.
     * @param key
     * @return the previous value, if there was any
     */
    public ValueType remove(Object key){
        return store.remove(key);
    }

    /**
     * How many entries this cache map contains.
     */
    public int size(){
        this.clearExpired();
        return store.size();
    }
}

class ExpiredNode <K> {
    private K key;
    private long expired;

    ExpiredNode(K nodekey, long ttl){
        key = nodekey;
        long curtime = Clock.getTime();
        expired = curtime + ttl;
//        System.out.println(nodekey+" expired in "+expired);
    }
    K getKey() {
        return key;
    }
    long getExpired() {
        return expired;
    }
}