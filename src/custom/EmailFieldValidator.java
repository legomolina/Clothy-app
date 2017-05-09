package custom;


import javafx.scene.control.TextInputControl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFieldValidator extends CustomRequiredFieldValidator {
    private static final Pattern EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public EmailFieldValidator(String message) {
        super(message);
    }

    protected void eval() {
        if (this.srcControl.get() instanceof TextInputControl)
            this.evalTextInputField();
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) this.srcControl.get();

        if (validateEmail(textField.getText()))
            this.hasErrors.set(false);
        else
            this.hasErrors.set(true);
    }

    private boolean validateEmail(String emailStr) {
        Matcher matcher = EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
