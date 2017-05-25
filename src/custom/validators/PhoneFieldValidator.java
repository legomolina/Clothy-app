package custom.validators;


import javafx.scene.control.TextInputControl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneFieldValidator extends CustomRequiredFieldValidator {
    private static final Pattern PHONE_REGEX = Pattern.compile("^(\\+[1-9]{1,3}\\s)?[0-9]{9,14}$", Pattern.MULTILINE);

    public PhoneFieldValidator(String message) {
        super(message);
    }

    protected void eval() {
        if (this.srcControl.get() instanceof TextInputControl)
            this.evalTextInputField();
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) this.srcControl.get();

        if (textField.getText().isEmpty() || textField.getText().equals(""))
            this.hasErrors.set(false);
        else {
            if (validatePhone(textField.getText()))
                this.hasErrors.set(false);
            else
                this.hasErrors.set(true);
        }
    }

    private boolean validatePhone(String phoneStr) {
        Matcher matcher = PHONE_REGEX.matcher(phoneStr);
        return matcher.find();
    }
}
