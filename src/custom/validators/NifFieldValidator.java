package custom.validators;


import javafx.scene.control.TextInputControl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NifFieldValidator extends CustomRequiredFieldValidator {
    private static final Pattern NIF_REGEX = Pattern.compile("^\\d{8}-?[A-z]{1}$", Pattern.MULTILINE);

    public NifFieldValidator(String message) {
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
            if (validateNif(textField.getText()))
                this.hasErrors.set(false);
            else
                this.hasErrors.set(true);
        }
    }

    private boolean validateNif(String nifStr) {
        Matcher matcher = NIF_REGEX.matcher(nifStr);
        return matcher.find();
    }
}
