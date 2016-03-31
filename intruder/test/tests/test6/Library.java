package test6; 

public class Library {
    MyField f1, f2;

    public Library() {
        setFirstField(new MyField()); 
        setSecondField(new MyField()); 
    }

    private void setFirstField(MyField f) {
        this.f1 = f; 
    }

    private void setSecondField(MyField f) {
        this.f2 = f; 
    }

    public void foo(){
        f1.bar();
        f2.bar(); 
    }
}

class MyField {
    int field = 10;

    public void bar() {
        // race on field
        field = -1;
        int x = field;
    }
    
    public void zee() {
        field = 10;
    }
}
