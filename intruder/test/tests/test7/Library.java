package test7; 

public class Library { 
    B f;

    Library() {
       f = new B(); 
    }

    public void setF(B f) {
        this.f = f;
    }

    public void foo(){
        f.foo();
    }
}

class B {
    C f;
  
    B() {
        f = new C(); 
    }

    public void setF(C f){
        this.f = f;
    }

    public void foo(){
        f.foo();
    }
}

class C {
    D f;

    C() {
        f = new D(); 
    }

    public void setF(D f){
        this.f = f;
    }

    public void foo() {
        f.foo();
    }
}


class D {
    int racyField = 0;

    public void foo(){
       racyField++; 
    }
}
