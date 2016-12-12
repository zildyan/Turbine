package hr.modulit.validation;

import hr.modulit.dto.UserData;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserData userData = (UserData) obj;
        return userData.getPassword().equals(userData.getMatchingPassword());
    }
}
