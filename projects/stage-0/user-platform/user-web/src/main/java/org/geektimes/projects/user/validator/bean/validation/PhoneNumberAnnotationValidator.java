package org.geektimes.projects.user.validator.bean.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 电话号码校验器
 *
 * @author wenhai
 * @date   2021/3/10
 */
public class PhoneNumberAnnotationValidator implements ConstraintValidator<PhoneNumber, String> {

    private int length;

    @Override
    public void initialize(PhoneNumber annotation) {
        this.length = annotation.length();

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.length() != length) {
           return false;
        }
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
