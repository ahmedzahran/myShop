package org.zahran.myshop.admin.user;
import org.zahran.myshop.entities.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidation implements ConstraintValidator<PasswordNotNullOnFirstSave,User> {

    @Override
    public void initialize(PasswordNotNullOnFirstSave constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {


        boolean isValid = true;

        if (user.getId() == null ){
            if (user.getPassword() == null){
                isValid = false;
            }
            if (user.getPassword().isEmpty()){
                isValid = false;
            }
        }

        if (user.getId() != null && !user.getPassword().isEmpty()){
            if (user.getPassword().length() > 64 || user.getPassword().length() < 3) {
                isValid = false;
            }
        }

        if(!isValid){
            constraintValidatorContext.buildConstraintViolationWithTemplate("password must between 3 and 64")
                    .addPropertyNode("password")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
