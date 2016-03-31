import java.awt.*;
import java.awt.event.*;
import org.exoplatform.portal.application.ApplicationStatistic;

public class ExoApplTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            ApplicationStatistic as = new ApplicationStatistic("sss");
	    as.executionCount();
	    long x = 10;
	    as.logTime(x);

	}
}
