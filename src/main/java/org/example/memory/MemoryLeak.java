package org.example.memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryLeak {
    public static List<Double> data = new ArrayList<>();
    public void processLeakedRecord(){

        for (int i=0;i<10000000;i++){
            data.add(Math.random());
        }
        System.out.println("print 2");
    }

    public static void main(String[] args) {
        System.out.println("Print 1");
        new MemoryLeak().processLeakedRecord();
        System.out.println("Print 3");
    }
}
