package test8; 

//To complicated without proper comments. 

public class B {
    public C f = new C();
  
    
    public synchronized void setF(C f) {
        this.f = f;
    }


    public synchronized void foo(){
        f.foo();
    }
}

