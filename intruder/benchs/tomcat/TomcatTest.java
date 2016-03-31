import java.awt.*;
import java.awt.event.*;
import org.apache.catalina.loader.WebappClassLoader;

public class TomcatTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
		
            WebappClassLoader wcl = new WebappClassLoader();
	    try {
		wcl.start();
	        wcl.findClass("org.apache.tomcat.util.IntrospectionUtils");
	    } catch (Exception e) { e.printStackTrace();}
	    try {
	        wcl.findClass("org.apache.tomcat.util.IntrospectionUtils");
	    } catch (Exception e) { e.printStackTrace();}
			
	}

}
