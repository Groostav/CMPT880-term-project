import java.awt.*;
import java.awt.event.*;
import com.amazon.carbonado.cursor.SkipCursor;
import com.amazon.carbonado.cursor.*;

public class CarbonadoTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            SingletonCursor<Integer> emp = new SingletonCursor<Integer>(new Integer(10));
            long l = 10;
	    SkipCursor<Integer> cursor = new SkipCursor(emp, 10);
	    SkipCursor<Integer> cursor1 = new SkipCursor(emp, 10);
	    try {
	      cursor.hasNext();
	      cursor1.next();
	      //cursor.skipNext();
            } catch (Exception e) { }
	}
}
