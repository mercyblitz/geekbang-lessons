package org.geekbang.live.lessions.design.patterns;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreationalPatternsDemo {

    public static void main(String[] args) {
        // 抽象工厂
        // 工厂方法
        // 创造器模式
        // 原型
        // 单例


        // Builder 模式
        // 抽象工厂 CharSequence#toString()
        StringBuilder stringBuilder = new StringBuilder(); // 单例

        CharSequence cs = stringBuilder;

        // CharSequence 超接口 - String、StringBuilder、StringBuffer

        // 方法是抽象的，java.lang.CharSequence 接口
        String value = cs.toString(); // 创建抽象工厂 -> 对象是原型


        // Builder -> Fluent API -> Stream
        List<String> values = Arrays.asList("1","2","3");

        values.stream().map(String::toString).map(String::hashCode)
                .collect(Collectors.toList());
                //.reduce(Integer::sum); // Collection -> Integer

        // 工厂模式状态性 ：有状态( Cache )、无状态(对象创建是Prototype)
        //  有状态（可变、不可变）
        //  可变：StringBuilder
        //  不可变：Stream 每个方法尽管返回 Stream 对象，但是每个对象是不同的，并且对象状态是不变的
        //  Immutable
        //  无状态
        // ThreadFactory ->
        // 静态的方法
    }

    public static void echo(CharSequence value) {

    }

    // 工厂方法命名前缀
    // build
    // new
    // to
    // create

    public static String toString(Object value){ // 工厂方法
        return value.toString();
    }
}
