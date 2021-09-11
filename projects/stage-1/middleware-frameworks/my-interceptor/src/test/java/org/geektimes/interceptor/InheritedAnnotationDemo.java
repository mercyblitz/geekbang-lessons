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
package org.geektimes.interceptor;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class InheritedAnnotationDemo {

    public static void main(String[] args) {
        Logging logging = A.class.getAnnotation(Logging.class);
        System.out.println(logging);

        logging = B.class.getAnnotation(Logging.class);
        System.out.println(logging);
    }
}

@Logging(name = "A")
class A { // Super Class

}

class B extends A { // B is A

}
