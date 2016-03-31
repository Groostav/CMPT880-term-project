package test12;

public class Library { 
    int x = 0; int y = 0;
     public void foo() {

     incX();
 
     incY();
    }

    public synchronized void incX() {
       x++;
    }

    public synchronized void incY() {
       y++;
    }
}
