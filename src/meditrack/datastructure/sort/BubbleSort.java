package meditrack.datastructure.sort;

/**
 * Implementation of the Bubble Sort algorithm
 * @param <T> The type of objects to be sorted
 */
public class BubbleSort<T extends Comparable<? super T>> implements SortingAlgorithm<T> {
    private long comparisonCount;
    private long swapCount;
    
    public BubbleSort() {
        resetCounters();
    }
    
    @Override
    public void sort(T[] array) {
        resetCounters();
        
        for (int lastIndex = array.length - 1; lastIndex > 0; lastIndex--) {
            for (int i = 0; i < lastIndex; i++) {
                comparisonCount++;
                
                if (array[i].compareTo(array[i + 1]) > 0) {
                    swapElements(array, i, i + 1);
                }
            }
        }
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
        return "Bubble Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
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