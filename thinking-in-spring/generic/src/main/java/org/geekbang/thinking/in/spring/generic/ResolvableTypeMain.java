package org.geekbang.thinking.in.spring.generic;

import org.springframework.core.ResolvableType;

import java.util.HashMap;
import java.util.List;

/**
 * @author xiaoheitalk
 * @type ResolvableTypeMain
 * @date 2020-7-23 23:58
 */
public class ResolvableTypeMain {
    public static void main(String[] args) throws NoSuchFieldException {
        new ResolvableTypeMain().example();
    }
    private HashMap<Integer, List<String>> myMap;

    public void example() throws NoSuchFieldException {
        ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("myMap"));
        System.out.println("t.getSuperType() = " + t.getSuperType());
        System.out.println("t.asMap() = " + t.asMap());
        System.out.println("t.getGeneric(0).resolve() = " + t.getGeneric(0).resolve());
        System.out.println("t.getGeneric(1).resolve() = " + t.getGeneric(1).resolve());
        System.out.println("t.getGeneric(1) = " + t.getGeneric(1));
        System.out.println("t.resolveGeneric(1, 0) = " + t.resolveGeneric(1, 0));
    }
}
