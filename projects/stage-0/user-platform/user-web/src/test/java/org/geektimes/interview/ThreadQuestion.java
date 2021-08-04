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
package org.geektimes.interview;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class ThreadQuestion {

    public static void main(String[] args) throws Exception {
//        threadJoin();
//        threadWait();
//        threadPriority();
//        threadSequence();
//        threadCondition();
        threadWaitAndNotify();
    }

    private static void threadWaitAndNotify() {

        Object monitor2 = new Object();

        Object monitor3 = new Object();

        // case 1 : t1 > t2
        // t2
        // case 2 : t2 > t1
        // t2 wait -> t1 monitor2.notify -> t2
        Thread t1 = new Thread(() -> {
            System.out.println("T1");
            synchronized (monitor2) {
                monitor2.notify();
            }
        });

        Thread t2 = new Thread(() -> {
            while (t1.isAlive()) {
                synchronized (monitor2) {
                    try {
                        monitor2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("T2");
            synchronized (monitor3) {
                monitor3.notify();
            }
        });

        // case t3 > t2 -> t3 wait t2 notify
        // t2 -> t1
        // t1 -> t2 notify t3 m3 -> t3
        // t1 t2 t3

        // t2 wait t1 notify -> t1 notify m2 -> t2
        // t1 t2 t3


        Thread t3 = new Thread(() -> {
            while (t2.isAlive()) {
                synchronized (monitor3) {
                    try {
                        monitor3.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("T3");
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private static void threadCondition() {

        Thread t1 = new Thread(() -> {
            System.out.println("T1");
        });

        Thread t2 = new Thread(() -> {
            wait(t1);
            System.out.println("T2");
        });

        Thread t3 = new Thread(() -> {
            wait(t1);
            wait(t2);
            System.out.println("T3");
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private static void wait(Thread thread) {
        while (thread.isAlive()) {
            synchronized (thread) {
                try {
                    thread.wait(); // 当线程执行结束后，Thread wait 状态会被直接唤起
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void threadSequence() {

        AtomicBoolean t1Started = new AtomicBoolean();
        AtomicBoolean t2Started = new AtomicBoolean();

        Thread t1 = new Thread(() -> {
            System.out.println("T1");
            t1Started.set(true);
        });

        Thread t2 = new Thread(() -> {
            while (!t1Started.get()) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("T2");
            t2Started.set(true);
        });

        Thread t3 = new Thread(() -> {
            while (!t1Started.get() || !t2Started.get()) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("T3");
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private static void threadPriority() {
        Thread t1 = new Thread(() -> System.out.println("T1"));
        t1.setPriority(Thread.MAX_PRIORITY);

        Thread t2 = new Thread(() -> System.out.println("T2"));
        t2.setPriority(Thread.NORM_PRIORITY);

        Thread t3 = new Thread(() -> System.out.println("T3"));
        t3.setPriority(Thread.MIN_PRIORITY);

        t1.start();
        t2.start();
        t3.start();
    }

    private static void threadWait() throws InterruptedException {
        Thread t1 = new Thread(() -> System.out.println("T1"));

        Thread t2 = new Thread(() -> System.out.println("T2"));

        Thread t3 = new Thread(() -> System.out.println("T3"));

        t1.start();
        synchronized (t1) {
            t1.wait();
        }

        t2.start();
        synchronized (t2) {
            t2.wait();
        }

        t3.start();
        synchronized (t3) {
            t3.wait();
        }
    }

    private static void threadJoin() throws InterruptedException {
        Thread t1 = new Thread(() -> System.out.println("T1"));

        Thread t2 = new Thread(() -> System.out.println("T2"));

        Thread t3 = new Thread(() -> System.out.println("T3"));

        t1.start();
        t1.join();

        t2.start();
        t2.join();

        t3.start();
        t3.join();
    }
}
