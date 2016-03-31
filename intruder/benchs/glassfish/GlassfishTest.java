import java.awt.*;
import java.awt.event.*;
import org.glassfish.admin.amx.impl.mbean.AMXImplBase;
import com.sun.appserv.management.util.jmx.JMXUtil;
import javax.management.ObjectName;
public class GlassfishTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            ObjectName on = JMXUtil.newObjectName("com.example:type=Hello");
	    AMXImplBase ab = new AMXImplBase(on);
	    ab.getListenerCount();	
	    ab.postDeregister();
	    ab.getListenerCount();	

	}
}
