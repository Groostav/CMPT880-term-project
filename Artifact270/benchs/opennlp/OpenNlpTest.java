import java.awt.*;
import java.awt.event.*;
import opennlp.tools.cmdline.PerformanceMonitor;

public class OpenNlpTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
	     PerformanceMonitor pm = new PerformanceMonitor("sss");
             pm.start();
	     pm.incrementCounter(10);
	     pm.incrementCounter();
			
	}
}
