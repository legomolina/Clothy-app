package custom.validators;


import controllers.database.EmployeesMethods;
import javafx.scene.control.TextInputControl;
import models.Employee;

public class EmployeeIdExistsValidator extends CustomRequiredFieldValidator {
    public EmployeeIdExistsValidator(String message) {
        super(message);
    }

    protected void eval() {
        if (this.srcControl.get() instanceof TextInputControl)
            this.evalTextInputField();
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) this.srcControl.get();

        int textFieldEmployeeId = Integer.parseInt(textField.getText());
        this.hasErrors.set(!employeeExists(textFieldEmployeeId));
    }

    private boolean employeeExists(int employeeId) {
        Employee employee = EmployeesMethods.getEmployee(employeeId);

        return employee != null;
    }
}
