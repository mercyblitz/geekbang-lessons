package org.geektimes.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class DefaultPublisher<T> implements Publisher<T> {

    private List<SubscriberWrapper> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> s) {
        DefaultSubscription subscription = new DefaultSubscription(s);
        // 当订阅者订阅时
        s.onSubscribe(subscription);
        subscribers.add(new SubscriberWrapper(s, subscription));
    }

    public void publish(T data) {
        // 广播
        subscribers.forEach(subscriber -> {

            SubscriberWrapper subscriberWrapper = (SubscriberWrapper) subscriber;
//                    SubscriberWrapper.class.cast(subscriber);

            DefaultSubscription subscription = subscriberWrapper.getSubscription();

            // 判断当前 subscriber 是否 cancel 数据发送
            if (subscription.isCanceled()) {
                System.err.println("本次数据发布已忽略，数据为：" + data);
                return;
            }

            // 继续发送
            subscriber.onNext(data);
        });
    }

    public void error(Throwable error) {
        // 广播
        subscribers.forEach(subscriber -> {
            subscriber.onError(error);
        });
    }

    public void complete() {
        // 广播
        subscribers.forEach(subscriber -> {
            subscriber.onComplete();
        });
    }

    public static void main(String[] args) {
        DefaultPublisher publisher = new DefaultPublisher();

        publisher.subscribe(new DefaultSubscriber());

        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }
}
