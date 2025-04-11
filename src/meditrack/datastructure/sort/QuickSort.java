package meditrack.datastructure.sort;

/**
 * Implementation of the Quick Sort algorithm
 * @param <T> The type of objects to be sorted
 */
public class QuickSort<T extends Comparable<? super T>> implements SortingAlgorithm<T> {
    private long comparisonCount;
    private long swapCount;
    
    public QuickSort() {
        resetCounters();
    }
    
    @Override
    public void sort(T[] array) {
        resetCounters();
        quickSort(array, 0, array.length - 1);
    }
    
    /**
     * Recursively sorts a portion of an array using quicksort
     * @param array The array to be sorted
     * @param first The index of the first element in the portion
     * @param last The index of the last element in the portion
     */
    private void quickSort(T[] array, int first, int last) {
        if (first < last) {
            int pivotIndex = partition(array, first, last);
            quickSort(array, first, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, last);
        }
    }
    
    /**
     * Partitions an array for quicksort
     * @param array The array to be partitioned
     * @param first The index of the first element in the portion
     * @param last The index of the last element in the portion
     * @return The final position of the pivot element
     */
    private int partition(T[] array, int first, int last) {
        T pivot = array[first]; // Use first element as pivot
        int up = first;
        int down = last;
        
        do {
            // Find an element greater than or equal to the pivot
            while ((up < last) && (array[up].compareTo(pivot) <= 0)) {
                up++;
                comparisonCount++;
            }
            
            // Find an element less than or equal to the pivot
            while (array[down].compareTo(pivot) > 0) {
                down--;
                comparisonCount++;
            }
            
            if (up < down) { // If the elements are on the wrong side
                swapElements(array, up, down);
            }
        } while (up < down);
        
        // Place the pivot in its final position
        swapElements(array, first, down);
        
        return down;
    }
    
    /**
     * Swaps two elements in an array
     * @param array The array containing the elements
     * @param i The index of the first element
     * @param j The index of the second element
     */
    private void swapElements(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        swapCount++;
    }
    
    @Override
    public String getName() {
        return "Quick Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n log n) average, O(n²) worst case";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(log n)";
    }
    
    @Override
    public long getComparisonCount() {
        return comparisonCount;
    }
    
    @Override
    public long getSwapCount() {
        return swapCount;
    }
    
    @Override
    public void resetCounters() {
        comparisonCount = 0;
        swapCount = 0;
    }
}