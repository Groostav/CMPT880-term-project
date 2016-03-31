package test4; 

public class Library {
   int x = 0;
    
    public void foo() {
       x = 5;
       bar();
    }  
     
    public synchronized void bar() {
        x++;
        
    }
}
