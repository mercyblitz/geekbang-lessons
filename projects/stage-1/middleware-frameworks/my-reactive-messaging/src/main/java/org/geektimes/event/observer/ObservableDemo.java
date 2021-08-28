/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.event.observer;

import java.util.Observable;

/**
 * {@link Observable} Demo
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ObservableDemo {

    public static void main(String[] args) {
        MyObservable observable = new MyObservable();
        // 增加订阅者（观察者）
        observable.addObserver((o, msg) -> {
            System.out.printf("Observable[%s] notifies a message[%s]\n", o, msg);
        });

        observable.notifyObservers("Hello,World");
    }
}

class MyObservable extends Observable {

    @Override
    public void notifyObservers(Object msg) {
        setChanged();
        super.notifyObservers(msg);
    }
//
//    @Override
//    public void setChanged() {
//        super.setChanged();
//    }
}
