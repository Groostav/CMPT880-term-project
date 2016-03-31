import java.awt.*;
import java.awt.event.*;
import org.apache.lucene.index.MergeRateLimiter;
public class LuceneTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            MergeRateLimiter lim = new MergeRateLimiter(null);
            long bytes = 10;
	    try {
	      lim.pause(10);
            } catch ( Exception e) {
               e.printStackTrace();
            }

            lim.getTotalBytesWritten();

	}
}
