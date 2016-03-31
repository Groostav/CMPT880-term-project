package test9;

public class Library { 
    static int field = 10;
    public static synchronized void foo(Library a){
        a.field++;
    }
}
