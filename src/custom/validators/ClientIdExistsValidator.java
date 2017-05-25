package custom.validators;


import controllers.database.ClientsMethods;
import javafx.scene.control.TextInputControl;
import models.Client;

public class ClientIdExistsValidator extends CustomRequiredFieldValidator {

    public ClientIdExistsValidator(String message) {
        super(message);
    }

    protected void eval() {
        if (this.srcControl.get() instanceof TextInputControl)
            this.evalTextInputField();
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) this.srcControl.get();

        int textFieldClientId = Integer.parseInt(textField.getText());
        this.hasErrors.set(!clientExists(textFieldClientId));

    }

    private boolean clientExists(int clientId) {
        Client client = ClientsMethods.getClient(clientId);

        return client != null;
    }
}
