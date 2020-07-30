package org.geekbang.live.lessions.design.patterns;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.stream.IntStream;

public class ProxyPatternDemo {

    public static void main(String[] args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // 动态代理（CharSequence）没有具体实现类
        CharSequence cs=  (CharSequence) Proxy.newProxyInstance(classLoader,
                new Class[]{CharSequence.class, Closeable.class},
                new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });

        // JDK 内建的字节码提升（字节码生成）
        // newProxyInstance -> Dynamic Class( ClassLoader ) implements Interface1, Interface2, ... {
        //
        // }

        // 静态（具体）代理
        // MyCharSequence is CharSequence
        // String is CharSequence

        CharSequence cs1 = new MyCharSequence("Hello,World");

        // 适配器
        // 适配对象和被适配对象之间的是没有类型关系
    }

    static class MyCharSequence implements CharSequence { // 类似于装饰器模式

        // 装饰器模式 implements + more

        private final CharSequence cs;

        MyCharSequence(CharSequence cs) {
            this.cs = cs;
        }

        @Override
        public int length() {
            return cs.length();
        }

        @Override
        public char charAt(int index) {
            return 0;
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return null;
        }

        @Override
        public String toString() {
            return cs.toString();
        }

        @Override
        public IntStream chars() {
            return cs.chars();
        }

        @Override
        public IntStream codePoints() {
            return cs.codePoints();
        }
    }
}
