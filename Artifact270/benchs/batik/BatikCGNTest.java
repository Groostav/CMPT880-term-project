import java.awt.*;
import java.awt.event.*;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.*;

public class BatikCGNTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            CompositeGraphicsNode cgn = new CompositeGraphicsNode();
	    
            for (int i = 0; i< 10; i++) {
                TextNode tn = new TextNode();
                cgn.add(tn);
            }
            TextNode tn = new TextNode();
            //cgn.add(0, tn);
   	    cgn.remove(0);
	    cgn.ensureCapacity(1);

	}
}
