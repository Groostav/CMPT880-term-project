package test1; 

public class Library { 
    int x = 0;
     public void foo() {
      synchronized(this) { x++;}
    }
    
    public void bar(Library a) {
        a.foo();
        Library temp = new Library();
        temp.zee();
    }
    
    private void zee() {
        x++;
    }
    
}
