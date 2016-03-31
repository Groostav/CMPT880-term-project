import java.awt.*;
import java.awt.event.*;
import benchmarks.instrumented.java17.util.Vector;
import benchmarks.instrumented.java17.util.*;
import java.lang.Integer;

public class VectorTest{


	public static void main(String[] args){
		test();
	}
	
	/** 
	 * Test takes an option and runs the selected test case
	 */

	static void test() {
	
            // Create a String Buffer
	    {
            	Vector v1 = new Vector<Integer>();
	    	Vector v2 = new Vector<Integer>();
	    	for(Integer i = 0; i < 10; i ++) {
			v1.add(i); v2.add(2*i); 
            	} 
		v1.addAll(v2);
		v1.clear();
	    	for(Integer i = 0; i < 10; i ++) {
			v1.add(i); 
            	} 
	    }

	    {
            	Vector v3 = new Vector<Integer>();
	    	Vector v4 = new Vector<Integer>();
	    	for(Integer i = 0; i < 10; i ++) {
			v3.add(i); v4.add(i); 
            	} 
		v3.containsAll(v4);
	    }
	    {
            	Vector v5 = new Vector<Integer>();
	    	Vector v6 = new Vector<Integer>();
	    	for(Integer i = 0; i < 10; i ++) {
			v5.add(i); v6.add(i); 
            	} 
		v5.retainAll(v6);
	    }

	    {
            	Vector v7 = new Vector<Integer>();
	    	Vector v8 = new Vector<Integer>();
	    	for(Integer i = 0; i < 10; i ++) {
			v7.add(i); v8.add(2*i); 
            	} 
		v7.removeAll(v8);
		v7.clear();
	    	for(Integer i = 20; i >= 0; i --) {
			v7.add(i); 
            	} 
	    }
            
	    {
            	Vector v9 = new Vector<Integer>();
	    	for(Integer i = 0; i < 10; i ++) {
			v9.add(i);
            	} 
		v9.clear();
	    	for(Integer i = 0; i < 10; i ++) {
			v9.add(i); 
        	} 

	    }


			
	}
}
