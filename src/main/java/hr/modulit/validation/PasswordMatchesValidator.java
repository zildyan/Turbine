package hr.modulit.validation;

import hr.modulit.dto.AccountData;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        AccountData accountData = (AccountData) obj;
        return accountData.getPassword().equals(accountData.getMatchingPassword());
    }
}
