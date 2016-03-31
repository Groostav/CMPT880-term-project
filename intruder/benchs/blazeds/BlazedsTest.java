import java.awt.*;
import java.awt.event.*;
import flex.messaging.endpoints.StreamingHTTPEndpoint;
import flex.messaging.endpoints.BaseStreamingHTTPEndpoint;
import flex.messaging.*;

public class BlazedsTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            StreamingHTTPEndpoint ep = new StreamingHTTPEndpoint();
            StreamingHTTPEndpoint ep1 = new StreamingHTTPEndpoint();
	    MessageBroker msg = new MessageBroker();
	    MessageBroker msg1 = new MessageBroker();
	    try {
	       msg.start();
            } catch (Exception e) {}

	    try {
	       msg1.start();
            } catch (Exception e) {}
	    
	    ep.setId("id"); ep1.setId("id");
	    ep.setParent(msg); ep1.setParent(msg);
	    ep.setConnectionIdleTimeoutMinutes(2); ep1.setConnectionIdleTimeoutMinutes(2);
	    System.out.println(ep.isStarted());
	    ep1.start(); ep.start();
	    System.out.println(ep.isStarted());
	    ep.stop(); ep1.stop();
	    System.out.println(ep.isStarted());
	    ep.start();

	}
}
