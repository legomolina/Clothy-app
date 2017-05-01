package models;


import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

public class Employee extends RecursiveTreeObject<Employee> {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty surname;
    private StringProperty address;
    private StringProperty email;
    private StringProperty phone;
    private StringProperty loginName;
    private StringProperty loginPassword;
    private StringProperty loginType;
    private BooleanProperty loginActive;

    public Employee(int id, String name, String surname, String address, String email, String phone, String loginName, String loginPassword, String loginType, Boolean loginActive) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.address = new SimpleStringProperty(address);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.loginName = new SimpleStringProperty(loginName);
        this.loginPassword = new SimpleStringProperty(loginPassword);
        this.loginType = new SimpleStringProperty(loginType);
        this.loginActive = new SimpleBooleanProperty(loginActive);
    }

    public IntegerProperty getId() {
        return id;
    }

    public void setId(IntegerProperty id) {
        this.id = id;
    }

    public StringProperty getName() {
        return name;
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    public StringProperty getSurname() {
        return surname;
    }

    public void setSurname(StringProperty surname) {
        this.surname = surname;
    }

    public StringProperty getAddress() {
        return address;
    }

    public void setAddress(StringProperty address) {
        this.address = address;
    }

    public StringProperty getEmail() {
        return email;
    }

    public void setEmail(StringProperty email) {
        this.email = email;
    }

    public StringProperty getPhone() {
        return phone;
    }

    public void setPhone(StringProperty phone) {
        this.phone = phone;
    }

    public StringProperty getLoginName() {
        return loginName;
    }

    public void setLoginName(StringProperty loginName) {
        this.loginName = loginName;
    }

    public StringProperty getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(StringProperty loginPassword) {
        this.loginPassword = loginPassword;
    }

    public StringProperty getLoginType() {
        return loginType;
    }

    public void setLoginType(StringProperty loginType) {
        this.loginType = loginType;
    }

    public BooleanProperty getLoginActive() {
        return loginActive;
    }

    public void setLoginActive(BooleanProperty loginActive) {
        this.loginActive = loginActive;
    }
}
