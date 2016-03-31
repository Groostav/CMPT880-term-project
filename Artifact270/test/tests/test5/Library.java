package test5; 

public class Library {
    MyField f;

    public Library() {
        f = new MyField();
    }

    public void foo(){
        f.bar();
    }
}

class MyField {
    int field = 10;

    public synchronized void bar(){
        field++;
    }
}
