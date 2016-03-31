package test3; 

public class Library {
    int x = 0;
   
     public Library(int a) {
          sample(a);
     }
   
     public Library() {}

     public synchronized void foo() {
        x++;
     }
    
     public synchronized void sample(int a) {
	    x = a;
     }

}
