import java.awt.*;
import java.awt.event.*;
import hep.aida.bin.*;
import cern.colt.list.*;
public class ColtTest{

	/* List of API's to be tested : 
	 * 			1. correlation(DynamicBin1D other)  
	 * 			2. covariance(DynamicBin1D other) 
	 * 			3. equals(Object object) 
	 * 			4. sampleBootstrap(DynamicBin1D other, int resamples, RandomEngine randomGenerator, BinBinFunction1D function) 	
	 * 			Link to documentation and source: ../index.html 
	 */

	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
		// Create a DynamicBin1D
		DynamicBin1D bin1 = new DynamicBin1D ();
		DynamicBin1D bin2 = new DynamicBin1D ();
		DynamicBin1D bin3 = new DynamicBin1D ();
		Integer a = 1;
		//Create a array of double
//		double[] v1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9,10};
		double[] v1 = { 1, 2};
/*		cern.jet.random.engine.RandomEngine random = new cern.jet.random.engine.MersenneTwister();
		BinBinFunction1D diff = new BinBinFunction1D() {
			 public double apply(DynamicBin1D x, DynamicBin1D y) {return 0; }
		};
*/
		//Add the array to bin
		bin1.addAllOf(new DoubleArrayList(v1));
		bin2.addAllOf(new DoubleArrayList(v1));
		bin3.addAllOf(new DoubleArrayList(v1));
			
		bin1.correlation(bin2);
		bin1.covariance(bin2);
		
                cern.jet.random.engine.RandomEngine random = new cern.jet.random.engine.MersenneTwister();
                BinBinFunction1D diff = new BinBinFunction1D() {
                         public double apply(DynamicBin1D x, DynamicBin1D y) {return 0; }
                };

                bin1.equals(bin2);
		bin1.sampleBootstrap(bin2, a, random, diff);
		bin3.clear();

	}
}
