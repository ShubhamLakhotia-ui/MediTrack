package meditrack.datastructure.heap;

/**
 * An interface for the ADT priority queue.
 * @param <T> The type of objects stored in the queue
 */
public interface PriorityQueueInterface<T extends Comparable<? super T>> {
    /**
     * Adds a new entry to this priority queue.
     * @param newEntry An object to be added.
     */
    public void add(T newEntry);
    
    /**
     * Removes and returns the entry having the highest priority.
     * @return The object with the highest priority or null if the queue is empty.
     */
    public T remove();
    
    /**
     * Retrieves the entry having the highest priority.
     * @return The object with the highest priority or null if the queue is empty.
     */
    public T peek();
    
    /**
     * Detects whether this priority queue is empty.
     * @return True if the priority queue is empty.
     */
    public boolean isEmpty();
    
    /**
     * Gets the size of this priority queue.
     * @return The number of entries currently in the priority queue.
     */
    public int getSize();
    
    /**
     * Removes all entries from this priority queue.
     */
    public void clear();
}