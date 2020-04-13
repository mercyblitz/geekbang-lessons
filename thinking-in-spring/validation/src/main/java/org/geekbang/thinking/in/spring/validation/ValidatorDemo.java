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
package org.geekbang.thinking.in.spring.validation;

import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.context.MessageSource;
import org.springframework.validation.*;

import java.util.Locale;

import static org.geekbang.thinking.in.spring.validation.ErrorsMessageDemo.createMessageSource;

/**
 * 自定义 Spring {@link Validator} 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Validator
 * @since
 */
public class ValidatorDemo {


    public static void main(String[] args) {
        // 1. 创建 Validator
        Validator validator = new UserValidator();
        // 2. 判断是否支持目标对象的类型
        User user = new User();
        System.out.println("user 对象是否被 UserValidator 支持检验：" + validator.supports(user.getClass()));
        // 3. 创建 Errors 对象
        Errors errors = new BeanPropertyBindingResult(user, "user");
        validator.validate(user, errors);

        // 4. 获取 MessageSource 对象
        MessageSource messageSource = createMessageSource();

        // 5. 输出所有的错误文案
        for (ObjectError error : errors.getAllErrors()) {
            String message = messageSource.getMessage(error.getCode(), error.getArguments(), Locale.getDefault());
            System.out.println(message);
        }
    }

    static class UserValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return User.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            User user = (User) target;
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "id.required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
            String userName = user.getName();
            // ...
        }
    }
}
