package com.trpc;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class GenericTest {
    
    @Test
    @SuppressWarnings({"rawtypes", "unused"})
    public void StringTest() {
        Class clazz = (new ArrayList<Object>() {}).getClass();
        System.out.println(clazz);
        System.out.println(clazz.getSuperclass());
        System.out.println(clazz.getGenericSuperclass());
        System.out.println(((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
        try {
            Class obj = Class.forName("java.lang.Object");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
