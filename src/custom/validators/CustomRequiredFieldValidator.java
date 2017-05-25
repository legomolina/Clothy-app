package custom.validators;

import com.jfoenix.validation.RequiredFieldValidator;


public class CustomRequiredFieldValidator extends RequiredFieldValidator {
    public CustomRequiredFieldValidator(String message) {
        super();
        this.setMessage(message);
    }
}
