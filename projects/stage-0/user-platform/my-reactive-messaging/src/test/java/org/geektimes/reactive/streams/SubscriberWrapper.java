package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 组合模式 + 装饰器模式
 * @param <T>
 */
public class SubscriberWrapper<T> implements Subscriber<T> {

    private final Subscriber<T> subscriber; // 来自应用实现

    private final DefaultSubscription subscription; // 内部实现

    public SubscriberWrapper(Subscriber<T> subscriber,
                             DefaultSubscription subscription) {
        this.subscriber = subscriber;
        this.subscription = subscription;
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        subscriber.onError(t);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    public Subscriber<T> getSubscriber() {
        return subscriber;
    }

    public DefaultSubscription getSubscription() {
        return subscription;
    }
}
