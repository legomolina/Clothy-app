package custom;

import com.jfoenix.validation.DoubleValidator;

public class CustomDoubleValidator extends DoubleValidator {
    public CustomDoubleValidator(String message) {
        super();
        this.setMessage(message);
    }
}
