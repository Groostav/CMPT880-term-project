package test7;
import test7.Library;

public class Test {
    public static void main(String [] args) {
       Library a = new Library(); 
       a.foo(); 

       a = new Library();
       a.setF(new B()); 
       
       B b = new B(); 
       b.setF(new C()); 

       C c = new C(); 
       c.setF(new D()); 

       a.foo(); 
    }
}
