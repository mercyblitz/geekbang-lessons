package org.geektimes.reactive.message.demo;

import org.reactivestreams.Publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JdkDynamicProxyDemo {


    static class OutgoingMethodInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Type returnType = method.getGenericReturnType();

            if (returnType instanceof Class) { // 直接类型

            } else if (returnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) returnType;
                Type rawType = parameterizedType.getRawType();
                Type argType = parameterizedType.getActualTypeArguments()[0];
                if (rawType instanceof Class) {
                    Class rawReturnType = (Class) rawType;
                    if (Publisher.class.isAssignableFrom(rawReturnType)) {

                    }
                }

            }


            return null;
        }
    }
}
