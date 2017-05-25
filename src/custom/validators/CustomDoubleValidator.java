package custom.validators;

import com.jfoenix.validation.DoubleValidator;

public class CustomDoubleValidator extends DoubleValidator {
    public CustomDoubleValidator(String message) {
        super();
        this.setMessage(message);
    }
}
