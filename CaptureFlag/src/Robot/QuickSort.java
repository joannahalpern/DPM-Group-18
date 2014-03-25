package Robot;

/**
 * Working and tested sorting algorithm to sort any double array
 * 
 * original code taken and then tweaked from:
 * http://codereview.stackexchange.com/questions/4022/java-implementation-of-quick-sort
 */
public class QuickSort {

	/**
	 * Will sort an array
	 * @param array is the given array
	 * @param p - should make this 0
	 * @param r is the size of the array - 1
	 */
	public static void quickSort(double[] array, int p, int r) {
		if (p < r) {
			int q = partition(array, p, r);
			quickSort(array, p, q);
			quickSort(array, q + 1, r);
		}
	}

	private static int partition(double[] array, int p, int r) {
		double x = array[p];
		int i = p - 1;
		int j = r + 1; //this should be the length of the array

		while (true) {
			i++;
			while (i < r && array[i] < x)
				i++;
			j--;
			while (j > p && array[j] > x)
				j--;

			if (i < j)
				swap(array, i, j);
			else
				return j;
		}
	}

	private static void swap(double[] array, int i, int j) { //swap places of doubles at index i and index j
		double temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}