import java.awt.*;
import java.awt.event.*;
import org.exoplatform.portal.application.PortalStatistic;

public class ExoPortalTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            PortalStatistic ps = new PortalStatistic("sss");
	    ps.viewCount();
	    long x = 10;
	    ps.logTime(x);

	}
}
