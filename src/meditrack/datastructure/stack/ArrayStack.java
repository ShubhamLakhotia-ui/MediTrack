package meditrack.datastructure.stack;

import java.util.EmptyStackException;
import java.util.Arrays;

/**
 * A class of stacks whose entries are stored in an array.
 * @param <T> The type of objects stored in the stack
 */
public final class ArrayStack<T> implements StackInterface<T> {
    private T[] stack;    // Array of stack entries
    private int topIndex; // Index of top entry
    private static final int DEFAULT_CAPACITY = 50;
    private static final int MAX_CAPACITY = 10000;
    
    /**
     * Creates an empty stack with default capacity.
     */
    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates an empty stack with a given capacity.
     * @param initialCapacity The initial capacity of the stack.
     */
    public ArrayStack(int initialCapacity) {
        checkCapacity(initialCapacity);
        
        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        T[] tempStack = (T[])new Object[initialCapacity];
        stack = tempStack;
        topIndex = -1;
    }
    
    /**
     * Throws an exception if the client requests a capacity that is too large.
     */
    private void checkCapacity(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalStateException(
                "Attempt to create a stack whose capacity exceeds " +
                "allowed maximum of " + MAX_CAPACITY);
    }
    
    /**
     * Doubles the size of the array stack if it is full
     */
    private void ensureCapacity() {
        if (topIndex == stack.length - 1) { // If array is full, double its size
            int newLength = 2 * stack.length;
            checkCapacity(newLength);
            stack = Arrays.copyOf(stack, newLength);
        }
    }

    @Override
    public void push(T newEntry) {
        ensureCapacity();
        topIndex++;
        stack[topIndex] = newEntry;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        } else {
            T top = stack[topIndex];
            stack[topIndex] = null; // To help garbage collection
            topIndex--;
            return top;
        }
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        } else {
            return stack[topIndex];
        }
    }

    @Override
    public boolean isEmpty() {
        return topIndex < 0;
    }

    @Override
    public void clear() {
        // Remove references to help garbage collection
        for (int i = 0; i <= topIndex; i++) {
            stack[i] = null;
        }
        topIndex = -1;
    }
    
    /**
     * Gets the current size of the stack.
     * @return The number of entries currently in the stack.
     */
    public int size() {
        return topIndex + 1;
    }
}