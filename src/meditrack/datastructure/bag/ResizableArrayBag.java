package meditrack.datastructure.bag;

import java.util.Arrays;

/**
 * A class of bags whose entries are stored in a resizable array.
 * @param <T> The type of objects stored in the bag
 */
public final class ResizableArrayBag<T> implements BagInterface<T> {
    private T[] bag;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;
    private boolean integrityOK = false;
    
    /** Creates an empty bag whose initial capacity is 25. */
    public ResizableArrayBag() {
        this(DEFAULT_CAPACITY);
    }
    
    /** 
     * Creates an empty bag having a given initial capacity.
     * @param desiredCapacity The integer capacity desired.
     */
    public ResizableArrayBag(int desiredCapacity) {
        integrityOK = false;
        
        if (desiredCapacity <= MAX_CAPACITY) {
            // The cast is safe because the new array contains null entries.
            @SuppressWarnings("unchecked")
            T[] tempBag = (T[])new Object[desiredCapacity]; // Unchecked cast
            bag = tempBag;
            numberOfEntries = 0;
            integrityOK = true;
        }
        else {
            throw new IllegalStateException("Attempt to create a bag whose " +
                                           "capacity exceeds allowed maximum.");
        }
    }
    
    /**
     * Throws an exception if this object is not initialized.
     */
    private void checkIntegrity() {
        if (!integrityOK)
            throw new SecurityException("ArrayBag object is corrupt.");
    }
    
    /**
     * Throws an exception if the client requests a capacity that is too large.
     */
    private void checkCapacity(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a bag whose " +
                                           "capacity exceeds allowed " +
                                           "maximum of " + MAX_CAPACITY);
    }
    
    /**
     * Doubles the size of the array bag.
     */
    private void doubleCapacity() {
        int newLength = 2 * bag.length;
        checkCapacity(newLength);
        bag = Arrays.copyOf(bag, newLength);
    }
    
    @Override
    public int getCurrentSize() {
        return numberOfEntries;
    }
    
    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }
    
    @Override
    public boolean add(T newEntry) {
        checkIntegrity();
        
        if (isArrayFull()) {
            doubleCapacity();
        }
        
        bag[numberOfEntries] = newEntry;
        numberOfEntries++;
        
        return true;
    }
    
    @Override
    public T remove() {
        checkIntegrity();
        
        if (isEmpty()) {
            return null;
        }
        
        // Remove last entry
        T result = bag[numberOfEntries - 1];
        bag[numberOfEntries - 1] = null;
        numberOfEntries--;
        
        return result;
    }
    
    @Override
    public boolean remove(T anEntry) {
        checkIntegrity();
        
        int index = getIndexOf(anEntry);
        
        if (index < 0) {
            return false;
        }
        
        // Replace the removed entry with the last entry
        bag[index] = bag[numberOfEntries - 1];
        bag[numberOfEntries - 1] = null;
        numberOfEntries--;
        
        return true;
    }
    
    @Override
    public void clear() {
        checkIntegrity();
        
        // Clear references to help garbage collection
        for (int index = 0; index < numberOfEntries; index++) {
            bag[index] = null;
        }
        
        numberOfEntries = 0;
    }
    
    @Override
    public int getFrequencyOf(T anEntry) {
        checkIntegrity();
        int counter = 0;
        
        for (int index = 0; index < numberOfEntries; index++) {
            if (anEntry.equals(bag[index])) {
                counter++;
            }
        }
        
        return counter;
    }
    
    @Override
    public boolean contains(T anEntry) {
        checkIntegrity();
        return getIndexOf(anEntry) >= 0;
    }
    
    @Override
    public T[] toArray() {
        checkIntegrity();
        
        // The cast is safe because the new array contains null entries.
        @SuppressWarnings("unchecked")
        T[] result = (T[])new Object[numberOfEntries]; // Unchecked cast
        
        for (int index = 0; index < numberOfEntries; index++) {
            result[index] = bag[index];
        }
        
        return result;
    }
    
    /**
     * Returns true if the array bag is full, or false if not.
     */
    private boolean isArrayFull() {
        return numberOfEntries >= bag.length;
    }
    
    /**
     * Locates a given entry within the array bag.
     * @param anEntry The entry to be found.
     * @return The index of the entry if located, or -1 otherwise.
     */
    private int getIndexOf(T anEntry) {
        for (int index = 0; index < numberOfEntries; index++) {
            if (anEntry.equals(bag[index])) {
                return index;
            }
        }
        return -1;
    }
}