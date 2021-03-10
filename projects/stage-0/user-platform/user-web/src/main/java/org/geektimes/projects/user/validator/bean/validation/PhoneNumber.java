package org.geektimes.projects.user.validator.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * 电话号码检验注解
 *
 * @author wenhai
 * @date   2021/3/10
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneNumberAnnotationValidator.class)
public @interface PhoneNumber {
    int length() default 0;

    String message() default "电话号码只能是11位数字";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
}
