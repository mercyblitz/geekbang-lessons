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
import org.springframework.context.support.StaticMessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Locale;

/**
 * 错误文案示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Errors
 * @since
 */
public class ErrorsMessageDemo {

    public static void main(String[] args) {

        // 0. 创建 User 对象
        User user = new User();
        user.setName("小马哥");
        // 1. 选择 Errors - BeanPropertyBindingResult
        Errors errors = new BeanPropertyBindingResult(user, "user");
        // 2. 调用 reject 或 rejectValue
        // reject 生成 ObjectError
        // reject 生成 FieldError
        errors.reject("user.properties.not.null");
        // user.name = user.getName()
        errors.rejectValue("name", "name.required");

        // 3. 获取 Errors 中 ObjectError 和 FieldError
        // FieldError is ObjectError
        List<ObjectError> globalErrors = errors.getGlobalErrors();
        List<FieldError> fieldErrors = errors.getFieldErrors();
        List<ObjectError> allErrors = errors.getAllErrors();

        // 4. 通过 ObjectError 和 FieldError 中的 code 和 args 来关联 MessageSource 实现
        MessageSource messageSource = createMessageSource();

        for (ObjectError error : allErrors) {
            String message = messageSource.getMessage(error.getCode(), error.getArguments(), Locale.getDefault());
            System.out.println(message);
        }
    }

    static MessageSource createMessageSource() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("user.properties.not.null", Locale.getDefault(), "User 所有属性不能为空");
        messageSource.addMessage("id.required", Locale.getDefault(), "the id of User must not be null.");
        messageSource.addMessage("name.required", Locale.getDefault(), "the name of User must not be null.");
        return messageSource;
    }
}
