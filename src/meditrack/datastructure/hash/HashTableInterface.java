package meditrack.datastructure.hash;

/**
 * Interface for a hash table that maps keys to values
 * @param <K> The type of keys maintained by this map
 * @param <V> The type of mapped values
 */
public interface HashTableInterface<K, V> {
    
    /**
     * Associates the specified value with the specified key in this map
     * @param key The key with which the specified value is to be associated
     * @param value The value to be associated with the specified key
     * @return The previous value associated with key, or null if there was no mapping for key
     */
    V put(K key, V value);
    
    /**
     * Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or null if no mapping
     */
    V get(K key);
    
    /**
     * Removes the mapping for a key from this map if it is present
     * @param key The key whose mapping is to be removed from the map
     * @return The previous value associated with key, or null if there was no mapping for key
     */
    V remove(K key);
    
    /**
     * Returns true if this map contains a mapping for the specified key
     * @param key The key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    boolean containsKey(K key);
    
    /**
     * Returns the number of key-value mappings in this map
     * @return The number of key-value mappings in this map
     */
    int size();
    
    /**
     * Returns true if this map contains no key-value mappings
     * @return true if this map contains no key-value mappings
     */
    boolean isEmpty();
    
    /**
     * Removes all of the mappings from this map
     */
    void clear();
    
    /**
     * Returns the load factor of the hash table
     * @return The load factor (entries/capacity)
     */
    float getLoadFactor();
    
    /**
     * Returns the number of collisions that occurred during hash table operations
     * @return The number of collisions
     */
    int getCollisionCount();
    
    /**
     * Returns an array of all keys in the hash table
     * @return An array of all keys
     */
    K[] keys();
    
    /**
     * Returns an array of all values in the hash table
     * @return An array of all values
     */
    V[] values();
}