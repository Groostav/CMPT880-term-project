// Thid test tries to expose C at : cern/jet/random/engine/MersenneTwister.java#214
// with R at : cern/jet/random/engine/MersenneTwister.java#214
// accessing fied: [I3, mti]
import java.util.*;
import java.io.*; 
import intruder.util.*;
public class TestDriver7{ 

    public static List<List<Parameter>> col0;
    public static List<List<Parameter>> col1;
    public static List<Parameter> racyParameters0;
    public static List<Parameter> racyParameters1;

    /* Impose the constraints on the collected objects*/
    public static void imposeConstraint() { 

        Parameter racy = racyParameters0.get(3);
        racyParameters1.set(3, racy);
    }

    /*
    * Test for the race.
     */

    public static void main(String args[]) { 

        Initializer.initialize(7);


        /* Objects collected from run : 1
         Invocation of method : sampleBootstrap
        Line : 52 in ColtTest*/
        racyParameters0 = Initializer.getRacyObjects(0);	

        /* Objects collected from run : 2
         Invocation of method : sampleBootstrap
        Line : 52 in ColtTest*/
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
        hep.aida.bin.DynamicBin1D par2 = (hep.aida.bin.DynamicBin1D)paramList.get(1).returnStored();
        int par3 = (int)paramList.get(2).returnStoredInt();
        cern.jet.random.engine.RandomEngine par4 = (cern.jet.random.engine.RandomEngine)paramList.get(3).returnStored();
        hep.aida.bin.BinBinFunction1D par5 = (hep.aida.bin.BinBinFunction1D)paramList.get(4).returnStored();
        try {
             Thread.sleep(0* 10 * 1000);
             // Invocation leading to atomicity violation
            par1.sampleBootstrap(par2, par3, par4, par5);
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
        hep.aida.bin.DynamicBin1D par2 = (hep.aida.bin.DynamicBin1D)paramList.get(1).returnStored();
        int par3 = (int)paramList.get(2).returnStoredInt();
        cern.jet.random.engine.RandomEngine par4 = (cern.jet.random.engine.RandomEngine)paramList.get(3).returnStored();
        hep.aida.bin.BinBinFunction1D par5 = (hep.aida.bin.BinBinFunction1D)paramList.get(4).returnStored();
        try {
             Thread.sleep(1* 10 * 1000);
             // Invocation leading to atomicity violation
            par1.sampleBootstrap(par2, par3, par4, par5);
        } catch (Exception e) {            System.out.println("TestDriver Error");
            System.err.println("TestDriver Error");
            e.printStackTrace();
        }
    } 
}

