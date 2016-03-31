package test8; 

//To complicated without proper comments. 

public class Library { 
    B f = new B();

    public synchronized void setF(B f) {
        this.f = f;
    }

    public synchronized void foo(){
        f.foo();
    }
}
