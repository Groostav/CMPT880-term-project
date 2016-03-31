import java.awt.*;
import java.awt.event.*;
import com.hazelcast.map.impl.DefaultRecordStore;
import com.hazelcast.map.impl.*;
import com.hazelcast.config.*;

public class HazelCastTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            // Create a String Buffer
	
           Node node = new Node(
	   NodeEngineImpl eng = new NodeEngineImpl(node);	
           DefaultMapServiceContext context = new DefaultMapServiceContext(eng);
	     
           MapContainer cont = new MapContainer("sss", new MapConfig(), context);
           DefaultRecordStore store = new DefaultRecordStore(cont, 10);
			
	}
}
