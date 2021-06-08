package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DefaultSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;

    private int count = 0;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
    }

    @Override
    public void onNext(Object o) {
        if (++count > 2) { // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
            subscription.cancel();
            return;
        }
        System.out.println("收到数据：" + o);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("遇到异常：" + t);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据完成");
    }
}
