import java.awt.*;
import java.awt.event.*;
import org.apache.cassandra.locator.TokenMetadata;

public class CassandraTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
	    TokenMetadata token = new TokenMetadata();
	    token.getRingVersion();
            token.invalidateCachedRings();
	    System.out.println("Hello");
	}
}
