import java.awt.*;
import java.awt.event.*;
import benchmarks.instrumented.java17.lang.StringBuffer;
import benchmarks.instrumented.java17.lang.*;
public class StringBufferTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            // Create a String Buffer
	     
            StringBuffer sb1 = new StringBuffer("Welcome to SSS Lab");
            StringBuffer sb2 = new StringBuffer("Helllo world");
	    StringBuffer sb3 = new StringBuffer("Helllo world");
	    sb1.append("Hello");
	    sb1.append(sb2);
	    int length = sb1.length();
	    //sb1.charAt(5);
	    sb2.deleteCharAt(0);
	    //sb1.deleteCharAt(0);
	    sb1.delete(0, 3);
	    sb1.insert(0, sb3);
	   
	    //sb1.reverse();
			
	}
}
