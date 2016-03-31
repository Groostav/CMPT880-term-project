import java.awt.*;
import java.awt.event.*;
import org.cometd.client.ext.TimesyncClientExtension;
import org.cometd.client.ext.*;
import org.cometd.common.HashMapMessage;
import java.util.*;

public class CometdTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            TimesyncClientExtension t = new TimesyncClientExtension();
	    HashMapMessage message = new HashMapMessage();
	    message.getExt(true);
	    HashMap<String,Object> map = new HashMap<String,Object>();
	    map.put("tc", new Long(10));
	    map.put("ts", new Long(10));
	    map.put("p", new Integer(10));
            message.getExt().put("timesync", map);
	    t.rcvMeta(null, message);

	}
}
