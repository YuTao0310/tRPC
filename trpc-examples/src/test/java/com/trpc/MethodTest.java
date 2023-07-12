package com.trpc;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.trpc.hello.Hello;
import com.trpc.hello.HelloService;

public class MethodTest {

    public void method1(int i) {}

    public void method2 (int i, int j) {}

    @Test
    public void getMethodsTest() {
        System.out.println(HelloService.class.getMethods()[0]);
    }

    @Test
    void getMethodTest() {
        try {
            // for (Method m : MethodTest.class.getMethods()) System.out.println(m);
            Method method1 = MethodTest.class.getDeclaredMethods()[0];
            System.out.println("method1 is " + method1);
            Method method2 = MethodTest.class.getMethod("getMethodsTest", (Class<?>[] )null);
            System.out.println("method2 is " + method2);
            method2 = MethodTest.class.getMethod("getMethodsTest");
            System.out.println("method2 is " + method2);
            Method method3 = MethodTest.class.getMethod("method1", new Class[]{int.class});
            System.out.println("method3 is " + method3);
            method3 = MethodTest.class.getMethod("method1", int.class);
            System.out.println("method3 is " + method3);
            method3 = MethodTest.class.getMethod("method2", new Class[]{int.class, int.class});
            System.out.println("method3 is " + method3);
            method3 = MethodTest.class.getMethod("method2", int.class, int.class);
            System.out.println("method3 is " + method3);
            // method4无法正常获取到方法
            // Method method4 = HelloService.class.getMethod("com.trpc.hello.HelloService.hello", Hello.class);
            // System.out.println("method4 is " + method4);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
