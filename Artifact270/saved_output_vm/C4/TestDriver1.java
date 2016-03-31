// Thid test tries to expose C at : com/amazon/carbonado/cursor/SkipCursor.java#78
// with R at : com/amazon/carbonado/cursor/SkipCursor.java#82
// accessing fied: [I0, mSkip]
import java.util.*;
import java.io.*; 
import intruder.util.*;
public class TestDriver1{ 

    public static List<List<Parameter>> col0;
    public static List<List<Parameter>> col1;
    public static List<Parameter> racyParameters0;
    public static List<Parameter> racyParameters1;

    /* Impose the constraints on the collected objects*/
    public static void imposeConstraint() { 

        Parameter racy = racyParameters0.get(0);
        racyParameters1.set(0, racy);
    }

    /*
    * Test for the race.
     */

    public static void main(String args[]) { 

        Initializer.initialize(1);


        /* Objects collected from run : 1
         Invocation of method : hasNext
        Line : 24 in CarbonadoTest*/
        racyParameters0 = Initializer.getRacyObjects(0);	

        /* Objects collected from run : 2
         Invocation of method : next
        Line : 25 in CarbonadoTest*/
        racyParameters1 = Initializer.getRacyObjects(1);	


        imposeConstraint();


        Thread t0 = new Thread0(racyParameters0);
        Thread t1 = new Thread1(racyParameters1);

        t0.start();
        t1.start();

        try { 
            t0.join();
            t1.join();
        }
        catch(Exception e) { }
    }
}

class Thread0 extends Thread {
    List<Parameter> parameters;

    public Thread0(List<Parameter> paramList) {
        this.parameters = paramList;
    }
    public void run() {
        List<Parameter> paramList = parameters;
        com.amazon.carbonado.cursor.SkipCursor par1 = (com.amazon.carbonado.cursor.SkipCursor)paramList.get(0).returnStored();
        try {
             // Invocation leading to atomicity violation
            par1.hasNext();
        } catch (Exception e) {            System.out.println("TestDriver Error");
            System.err.println("TestDriver Error");
            e.printStackTrace();
        }
    } 
}


class Thread1 extends Thread {
    List<Parameter> parameters;

    public Thread1(List<Parameter> paramList) {
        this.parameters = paramList;
    }
    public void run() {
        List<Parameter> paramList = parameters;
        com.amazon.carbonado.cursor.SkipCursor par1 = (com.amazon.carbonado.cursor.SkipCursor)paramList.get(0).returnStored();
        try {
             // Invocation leading to atomicity violation
            par1.next();
        } catch (Exception e) {            System.out.println("TestDriver Error");
            System.err.println("TestDriver Error");
            e.printStackTrace();
        }
    } 
}

