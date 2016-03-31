// Thid test tries to expose C at : hep/aida/bin/DynamicBin1D.java#365
// with R at : hep/aida/bin/DynamicBin1D.java#343
// accessing fied: [I1, isIncrementalStatValid]
import java.util.*;
import java.io.*; 
import intruder.util.*;
public class TestDriver0{ 

    public static List<List<Parameter>> col0;
    public static List<List<Parameter>> col1;
    public static List<Parameter> racyParameters0;
    public static List<Parameter> racyParameters1;

    /* Impose the constraints on the collected objects*/
    public static void imposeConstraint() { 

        Parameter racy = racyParameters0.get(1);
        racyParameters1.set(0, racy);
    }

    /*
    * Test for the race.
     */

    public static void main(String args[]) { 

        Initializer.initialize(0);


        /* Objects collected from run : 1
         Invocation of method : equals
        Line : 51 in ColtTest*/
        racyParameters0 = Initializer.getRacyObjects(0);	

        /* Objects collected from run : 2
         Invocation of method : addAllOf
        Line : 39 in ColtTest*/
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
        hep.aida.bin.DynamicBin1D par1 = (hep.aida.bin.DynamicBin1D)paramList.get(0).returnStored();
        java.lang.Object par2 = (java.lang.Object)paramList.get(1).returnStored();
        try {
             Thread.sleep(0* 10 * 1000);
             // Invocation leading to atomicity violation
            par1.equals(par2);
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
        hep.aida.bin.DynamicBin1D par1 = (hep.aida.bin.DynamicBin1D)paramList.get(0).returnStored();
        cern.colt.list.DoubleArrayList par2 = (cern.colt.list.DoubleArrayList)paramList.get(1).returnStored();
        try {
             Thread.sleep(1* 10 * 1000);
             // Invocation leading to atomicity violation
            par1.addAllOf(par2);
        } catch (Exception e) {            System.out.println("TestDriver Error");
            System.err.println("TestDriver Error");
            e.printStackTrace();
        }
    } 
}

