package meditrack.datastructure.hash;

import meditrack.model.HealthcareProvider;
import java.util.Arrays;

/**
 * A hash table implementation for healthcare providers
 * Using separate chaining for collision resolution
 */
public class ProviderHashTable implements HashTableInterface<String, HealthcareProvider> {
    // Entry class for the hash table chain
    private static class Entry {
        String key;
        HealthcareProvider value;
        Entry next;
        
        Entry(String key, HealthcareProvider value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
    
    private Entry[] table;
    private int size;
    private int capacity;
    private int collisionCount;
    private static final int DEFAULT_CAPACITY = 101; // Prime number
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    /**
     * Creates a new hash table with default capacity
     */
    public ProviderHashTable() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates a new hash table with the specified capacity
     * @param capacity The initial capacity
     */
    @SuppressWarnings("unchecked")
    public ProviderHashTable(int capacity) {
        this.table = new Entry[capacity];
        this.size = 0;
        this.capacity = capacity;
        this.collisionCount = 0;
    }
    
    /**
     * Computes the hash code for a key
     * @param key The key to hash
     * @return The hash code (index in the table)
     */
    private int hash(String key) {
        // Simple hash function that sums the characters and applies modulo
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) % capacity;
        }
        return Math.abs(hash);
    }
    
    /**
     * Resizes the hash table when the load factor is exceeded
     */
    private void resize() {
        int oldCapacity = capacity;
        capacity = 2 * capacity; // Double the capacity
        
        Entry[] oldTable = table;
        @SuppressWarnings("unchecked")
        Entry[] newTable = new Entry[capacity];
        table = newTable;
        
        size = 0; // Will be recalculated during rehashing
        
        // Rehash all entries
        for (int i = 0; i < oldCapacity; i++) {
            Entry entry = oldTable[i];
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    @Override
    public HealthcareProvider put(String key, HealthcareProvider value) {
        if (getLoadFactor() > DEFAULT_LOAD_FACTOR) {
            resize();
        }
        
        int index = hash(key);
        Entry entry = table[index];
        
        // Check if the key already exists
        while (entry != null) {
            if (entry.key.equals(key)) {
                HealthcareProvider oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
            entry = entry.next;
        }
        
        // Key doesn't exist, add a new entry
        Entry newEntry = new Entry(key, value);
        
        // Check for collision
        if (table[index] != null) {
            collisionCount++;
            newEntry.next = table[index];
        }
        
        table[index] = newEntry;
        size++;
        
        return null;
    }

    @Override
    public HealthcareProvider get(String key) {
        int index = hash(key);
        Entry entry = table[index];
        
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        
        return null; // Key not found
    }

    @Override
    public HealthcareProvider remove(String key) {
        int index = hash(key);
        Entry current = table[index];
        Entry previous = null;
        
        while (current != null) {
            if (current.key.equals(key)) {
                // Found the key
                if (previous == null) {
                    // It's the first entry in the chain
                    table[index] = current.next;
                } else {
                    // It's not the first entry
                    previous.next = current.next;
                }
                
                size--;
                return current.value;
            }
            
            previous = current;
            current = current.next;
        }
        
        return null; // Key not found
    }

    @Override
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        collisionCount = 0;
    }

    @Override
    public float getLoadFactor() {
        return (float) size / capacity;
    }

    @Override
    public int getCollisionCount() {
        return collisionCount;
    }

    @Override
    public String[] keys() {
        String[] keys = new String[size];
        int index = 0;
        
        for (int i = 0; i < capacity; i++) {
            Entry entry = table[i];
            while (entry != null) {
                keys[index++] = entry.key;
                entry = entry.next;
            }
        }
        
        return keys;
    }

    @Override
    public HealthcareProvider[] values() {
        HealthcareProvider[] values = new HealthcareProvider[size];
        int index = 0;
        
        for (int i = 0; i < capacity; i++) {
            Entry entry = table[i];
            while (entry != null) {
                values[index++] = entry.value;
                entry = entry.next;
            }
        }
        
        return values;
    }
    
    /**
     * Returns the distribution of entries in the hash table
     * @return An array where each element is the number of entries in the corresponding bucket
     */
    public int[] getBucketDistribution() {
        int[] distribution = new int[capacity];
        
        for (int i = 0; i < capacity; i++) {
            int count = 0;
            Entry entry = table[i];
            
            while (entry != null) {
                count++;
                entry = entry.next;
            }
            
            distribution[i] = count;
        }
        
        return distribution;
    }
}