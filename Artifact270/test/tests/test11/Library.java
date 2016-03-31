package test11;

class MyField {
    int field = 10;

    public void bar(){
        field++;
    }
}

public class Library { 
    MyField b = new MyField();

    public synchronized void foo(){
        b.bar();
    }
}
