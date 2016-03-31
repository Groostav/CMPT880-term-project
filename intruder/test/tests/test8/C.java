package test8; 

//To complicated without proper comments. 

public class C {
    D f = new D();

    
    public synchronized void setF(D f){
        this.f = f;
    }

    public synchronized void foo() {
        f.foo();
    }
}

