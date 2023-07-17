package com.trpc;
import org.junit.jupiter.api.Test;

public class CloneTest implements Cloneable {
    
    @Test
    @SuppressWarnings("unused")
    public void test1() {
        CloneTest t1 = new CloneTest();
        try {
            CloneTest t2 = (CloneTest)t1.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
    }

}
