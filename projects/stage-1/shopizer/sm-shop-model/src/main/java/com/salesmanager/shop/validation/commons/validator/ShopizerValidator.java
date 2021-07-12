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
package com.salesmanager.shop.validation.commons.validator;

import com.salesmanager.shop.model.customer.SecuredShopPersistableCustomer;
import org.apache.commons.validator.*;
import org.apache.commons.validator.util.ValidatorUtils;

import java.io.InputStream;

/**
 * Shopzier Commons Validator
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ShopizerValidator {

//    public static void validateRequired(Object source, Field field) {
//        System.out.println(source + " , " + field);
//    }

    public static boolean validateRequired(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        return !GenericValidator.isBlankOrNull(value);
    }

    public static void main(String[] args) throws Exception {

        try (InputStream in = ShopizerValidator.class.getResourceAsStream("/META-INF/validation/commons-validator/validator-name-required.xml")) {

            // Create an instance of ValidatorResources to initialize from an xml file.
            ValidatorResources resources = new ValidatorResources(in);
            // Create bean to run test on.
            SecuredShopPersistableCustomer customer = new SecuredShopPersistableCustomer();
            customer.setUserName("mercyblitz");

            // Construct validator based on the loaded resources and the form key
            Validator validator = new Validator(resources, "customerForm");
            // add the name bean to the validator as a resource
            // for the validations to be performed on.
            validator.setParameter(Validator.BEAN_PARAM, customer);

            // Get results of the validation.
            ValidatorResults results = validator.validate();

            // throws ValidatorException (catch clause not shown here)

            ValidatorResult userNameResult = results.getValidatorResult("userName");
            ValidatorResult passwordResult = results.getValidatorResult("password");

            System.out.println("user name - result : " + userNameResult.getResult("required"));
            System.out.println("password - result : " + passwordResult.getResult("required"));

        }


    }
}
