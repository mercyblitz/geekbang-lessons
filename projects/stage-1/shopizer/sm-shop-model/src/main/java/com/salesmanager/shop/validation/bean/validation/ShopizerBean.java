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
package com.salesmanager.shop.validation.bean.validation;


import com.salesmanager.shop.validation.bean.validation.constraints.ShopizerName;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */

public class ShopizerBean {

    @NotNull(groups = A.class)
    @NotNull(groups = B.class, message = "{javax.validation.constraints.NotEmpty.message}")
    @ShopizerName(separator = ","
//            , message = "Hello,World"
    )
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();

        ShopizerBean bean = new ShopizerBean();
        // Group A 校验
        // 校验结果 ConstraintViolation
//        validate(validator, bean, A.class);
//        // Group B 校验
//        validate(validator, bean, B.class);
        // 非 Group A 和 非 Group B 校验
        validate(validator, bean);
    }

    static <T> void validate(Validator validator, T bean, Class... groupClasses) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean, groupClasses);
        for (ConstraintViolation<T> violation : violations) {
            System.out.println(violation.getMessage());
        }
    }
}

interface A {

}

interface B {

}

