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
package org.geekbang.thinking.in.spring.i18n;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * {@link MessageFormat} 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MessageFormat
 * @since
 */
public class MessageFormatDemo {

    public static void main(String[] args) {

        int planet = 7;
        String event = "a disturbance in the Force";

        String messageFormatPattern = "At {1,time,long} on {1,date,full}, there was {2} on planet {0,number,integer}.";
        MessageFormat messageFormat = new MessageFormat(messageFormatPattern);
        String result = messageFormat.format(new Object[]{planet, new Date(), event});
        System.out.println(result);

        // 重置 MessageFormatPattern
        // applyPattern
        messageFormatPattern = "This is a text : {0}, {1}, {2}";
        messageFormat.applyPattern(messageFormatPattern);
        result = messageFormat.format(new Object[]{"Hello,World", "666"});
        System.out.println(result);

        // 重置 Locale
        messageFormat.setLocale(Locale.ENGLISH);
        messageFormatPattern = "At {1,time,long} on {1,date,full}, there was {2} on planet {0,number,integer}.";
        messageFormat.applyPattern(messageFormatPattern);
        result = messageFormat.format(new Object[]{planet, new Date(), event});
        System.out.println(result);

        // 重置 Format
        // 根据参数索引来设置 Pattern
        messageFormat.setFormat(1,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"));
        result = messageFormat.format(new Object[]{planet, new Date(), event});
        System.out.println(result);
    }
}
