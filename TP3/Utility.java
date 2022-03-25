

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utility {

	public static NumberFormat nf = new DecimalFormat("#0.00");

	/**
	 * Test if a double is fractional
	 * @param d The tested double
	 * @return True if the double is fractional, false if it is an integer.
	 */
	public static boolean isFractional(double d) {
		return Math.abs(Math.round(d) - d) > 1E-6;
	}
	
	public void displaySolution() {
		
	}
	
	public static double[][] copyArray(double[][] input){
		
		double[][] copy = new double[input.length][];
		
		for(int i = 0; i < input.length; i++)
			copy[i] = copyArray(input[i]);
		
		return copy;
	}
	
	public static double[] copyArray(double[] input) {
		
		double[] copy = new double[input.length];
		
		for(int i = 0; i < input.length; i++)
			copy[i] = input[i];
		
		return copy;
	}

}
