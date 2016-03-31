package test8;
import test8.Library;

public class Test {
    public static void main(String [] args) {
       
        Library a = new Library();

        B b = new B();
       
        a = new Library();
        a.setF(b);

        
        C c = new C();
        c.setF(new D());

        b.setF(c);
        
       a.foo(); 
    }
}
