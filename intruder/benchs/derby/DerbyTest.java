import java.awt.*;
import java.awt.event.*;
import org.apache.derby.client.am.LogicalConnection;
import org.apache.derby.impl.jdbc.EmbedConnection30;
import org.apache.derby.jdbc.Driver30;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.impl.services.monitor.FileMonitor;
import org.apache.derby.iapi.services.monitor.Monitor;

public class DerbyTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            // Create a String Buffer
	      
	    FileMonitor fm = new FileMonitor(null, null);
            Monitor.setMonitor(fm);
            ContextService s = new ContextService();
            Driver30 dr = new Driver30();
	    LogicalConnection lc = null;
            try {
                EmbedConnection30 coneection = new EmbedConnection30(dr, "", null);	
                lc = new LogicalConnection(null, null);
            } catch (Exception e) {//e.printStackTrace();}
                }
	    lc.nullPhysicalConnection();
			
	}
}
