package meditrack.datastructure.bag;

/**
 * A class of bags whose entries are stored in a fixed-size array.
 * @param <T> The type of objects the bag will contain
 */
public final class ArrayBag<T> implements BagInterface<T> {
    private final T[] bag;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;
    private boolean integrityOK = false;

    /** Creates an empty bag whose initial capacity is 25. */
    public ArrayBag() {
        this(DEFAULT_CAPACITY);
    }

    /** 
     * Creates an empty bag having a given initial capacity.
     * @param desiredCapacity The integer capacity desired.
     */
    public ArrayBag(int desiredCapacity) {
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
     * Gets the current number of entries in this bag.
     * @return The integer number of entries currently in this bag.
     */
    public int getCurrentSize() {
        return numberOfEntries;
    }

    /** 
     * Sees whether this bag is empty.
     * @return True if this bag is empty, or false if not.
     */
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    /** 
     * Adds a new entry to this bag.
     * @param newEntry The object to be added as a new entry.
     * @return True if the addition is successful, or false if not.
     */
    public boolean add(T newEntry) {
        checkIntegrity();
        boolean result = true;
        
        if (isArrayFull()) {
            result = false;
        }
        else {
            bag[numberOfEntries] = newEntry;
            numberOfEntries++;
        }
        
        return result;
    }

    /**
     * Removes one unspecified entry from this bag, if possible.
     * @return Either the removed entry, if the removal was successful, or null.
     */
    public T remove() {
        checkIntegrity();
        T result = removeEntry(numberOfEntries - 1);
        return result;
    }

    /**
     * Removes one occurrence of a given entry from this bag, if possible.
     * @param anEntry The entry to be removed.
     * @return True if the removal was successful, or false otherwise.
     */
    public boolean remove(T anEntry) {
        checkIntegrity();
        int index = getIndexOf(anEntry);
        T result = removeEntry(index);
        return anEntry.equals(result);
    }

    /**
     * Removes all entries from this bag.
     */
    public void clear() {
        while (!isEmpty()) {
            remove();
        }
    }

    /**
     * Counts the number of times a given entry appears in this bag.
     * @param anEntry The entry to be counted.
     * @return The number of times anEntry appears in this bag.
     */
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

    /**
     * Tests whether this bag contains a given entry.
     * @param anEntry The entry to locate.
     * @return True if the bag contains anEntry, or false otherwise.
     */
    public boolean contains(T anEntry) {
        checkIntegrity();
        return getIndexOf(anEntry) > -1;
    }

    /**
     * Retrieves all entries that are in this bag.
     * @return A newly allocated array of all the entries in this bag.
     */
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
     * Locates a given entry within the array bag.
     * @param anEntry The entry to be found.
     * @return The index of the entry, if located, or -1 otherwise.
     */
    private int getIndexOf(T anEntry) {
        int where = -1;
        boolean found = false;
        int index = 0;
        
        while (!found && (index < numberOfEntries)) {
            if (anEntry.equals(bag[index])) {
                found = true;
                where = index;
            }
            index++;
        }
        
        return where;
    }

    /**
     * Removes and returns the entry at a given index within the array bag.
     * If no such entry exists, returns null.
     * @param givenIndex The index of the entry to be removed.
     * @return The entry if it exists, or null otherwise.
     */
    private T removeEntry(int givenIndex) {
        T result = null;
        
        if (!isEmpty() && (givenIndex >= 0)) {
            result = bag[givenIndex];                   // Entry to remove
            bag[givenIndex] = bag[numberOfEntries - 1]; // Replace with last entry
            bag[numberOfEntries - 1] = null;            // Remove last entry
            numberOfEntries--;
        }
        
        return result;
    }

    /**
     * Returns true if the array bag is full, or false if not.
     */
    private boolean isArrayFull() {
        return numberOfEntries >= bag.length;
    }
}