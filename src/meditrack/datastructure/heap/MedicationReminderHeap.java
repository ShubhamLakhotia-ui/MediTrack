package meditrack.datastructure.heap;

import meditrack.model.MedicationReminder;
import java.util.Arrays;

/**
 * A max heap implementation for medication reminders
 * Higher priority reminders are at the top of the heap
 */
public class MedicationReminderHeap implements PriorityQueueInterface<MedicationReminder> {
    private MedicationReminder[] heap;      // Array of heap entries
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;
    
    /**
     * Creates an empty heap with default capacity.
     */
    public MedicationReminderHeap() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates an empty heap with a given capacity.
     * @param initialCapacity The initial capacity of the heap.
     */
    public MedicationReminderHeap(int initialCapacity) {
        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        MedicationReminder[] tempHeap = (MedicationReminder[])new MedicationReminder[initialCapacity + 1];
        heap = tempHeap;
        numberOfEntries = 0;
    }
    
    /**
     * Throws an exception if the client requests a capacity that is too large.
     */
    private void checkCapacity(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalStateException(
                "Attempt to create a heap whose capacity exceeds " +
                "allowed maximum of " + MAX_CAPACITY);
    }
    
    /**
     * Doubles the size of the array heap if it is full
     */
    private void ensureCapacity() {
        if (numberOfEntries >= heap.length - 1) { // If array is full, double its size
            int newLength = 2 * heap.length;
            checkCapacity(newLength);
            heap = Arrays.copyOf(heap, newLength);
        }
    }

    @Override
    public void add(MedicationReminder newEntry) {
        // Add the new reminder to the end of the heap
        ensureCapacity();
        numberOfEntries++;
        int newIndex = numberOfEntries;
        heap[newIndex] = newEntry;
        
        // Restore the heap property by reheaping up
        reheapUp(newIndex);
    }
    
    /**
     * Reestablishes the heap property by percolating a new entry up to its proper position
     * @param newIndex The index of the new entry
     */
    private void reheapUp(int newIndex) {
        int parentIndex = newIndex / 2;
        
        // While we haven't reached the root and the parent has higher priority
        while (parentIndex > 0 && 
               heap[newIndex].compareTo(heap[parentIndex]) < 0) {
            // Swap the entries
            MedicationReminder temp = heap[parentIndex];
            heap[parentIndex] = heap[newIndex];
            heap[newIndex] = temp;
            
            // Update indices
            newIndex = parentIndex;
            parentIndex = newIndex / 2;
        }
    }

    @Override
    public MedicationReminder remove() {
        MedicationReminder result = null;
        
        if (!isEmpty()) {
            // Return the item at the root
            result = heap[1];
            
            // Move the last item to the root
            heap[1] = heap[numberOfEntries];
            heap[numberOfEntries] = null;
            numberOfEntries--;
            
            // Restore the heap property by reheaping down
            reheapDown(1);
        }
        
        return result;
    }
    
    /**
     * Reestablishes the heap property by percolating the entry at the specified position down
     * @param rootIndex The index of the entry to be reheaped down
     */
    private void reheapDown(int rootIndex) {
        boolean done = false;
        MedicationReminder orphan = heap[rootIndex];
        int leftChildIndex = 2 * rootIndex;
        
        while (!done && (leftChildIndex <= numberOfEntries)) {
            int largerChildIndex = leftChildIndex; // Assume left child is larger
            int rightChildIndex = leftChildIndex + 1;
            
            // If the right child exists and has higher priority than the left child
            if ((rightChildIndex <= numberOfEntries) && 
                heap[rightChildIndex].compareTo(heap[leftChildIndex]) < 0) {
                largerChildIndex = rightChildIndex;
            }
            
            // If the orphan has a lower priority than the larger child
            if (orphan.compareTo(heap[largerChildIndex]) > 0) {
                // Swap with the larger child
                heap[rootIndex] = heap[largerChildIndex];
                rootIndex = largerChildIndex;
                leftChildIndex = 2 * rootIndex;
            } else {
                done = true;
            }
        }
        
        // Place the orphan in its final position
        heap[rootIndex] = orphan;
    }

    @Override
    public MedicationReminder peek() {
        MedicationReminder result = null;
        
        if (!isEmpty()) {
            result = heap[1];
        }
        
        return result;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public int getSize() {
        return numberOfEntries;
    }

    @Override
    public void clear() {
        // Remove the references from the first n locations
        for (int index = 0; index <= numberOfEntries; index++) {
            heap[index] = null;
        }
        
        numberOfEntries = 0;
    }
    
    /**
     * Returns an array of all reminders in the heap, ordered by priority
     * @return Array of reminders in priority order
     */
    public MedicationReminder[] toSortedArray() {
        // Create a copy of the heap
        MedicationReminderHeap tempHeap = new MedicationReminderHeap(numberOfEntries);
        for (int i = 1; i <= numberOfEntries; i++) {
            tempHeap.add(heap[i]);
        }
        
        // Remove the entries in priority order
        MedicationReminder[] result = new MedicationReminder[numberOfEntries];
        for (int i = 0; i < numberOfEntries; i++) {
            result[i] = tempHeap.remove();
        }
        
        return result;
    }
}