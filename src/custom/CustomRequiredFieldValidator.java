package custom;

import com.jfoenix.validation.RequiredFieldValidator;


public class CustomRequiredFieldValidator extends RequiredFieldValidator {
    public CustomRequiredFieldValidator(String message) {
        this.setMessage(message);
    }
}
