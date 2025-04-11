package meditrack.datastructure.sort;

/**
 * Interface for sorting algorithms in the MediTrack application
 * @param <T> The type of objects to be sorted, must implement Comparable
 */
public interface SortingAlgorithm<T extends Comparable<? super T>> {
    
    /**
     * Sorts the given array using the algorithm
     * @param array The array to be sorted
     */
    void sort(T[] array);
    
    /**
     * Gets the name of the sorting algorithm
     * @return The name of the algorithm
     */
    String getName();
    
    /**
     * Gets the time complexity of the algorithm in Big-O notation
     * @return The time complexity
     */
    String getTimeComplexity();
    
    /**
     * Gets the space complexity of the algorithm in Big-O notation
     * @return The space complexity
     */
    String getSpaceComplexity();
    
    /**
     * Gets the count of comparisons performed during the last sort operation
     * @return The number of comparisons
     */
    long getComparisonCount();
    
    /**
     * Gets the count of swaps performed during the last sort operation
     * @return The number of swaps
     */
    long getSwapCount();
    
    /**
     * Resets the counters for a new sort operation
     */
    void resetCounters();
}