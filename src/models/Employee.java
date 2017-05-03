package models;


import javafx.beans.property.*;

public class Employee {
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

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public StringProperty getName() {
        return name;
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = new SimpleStringProperty(surname);
    }

    public StringProperty getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = new SimpleStringProperty(address);
    }

    public StringProperty getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = new SimpleStringProperty(email);
    }

    public StringProperty getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = new SimpleStringProperty(phone);
    }

    public StringProperty getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = new SimpleStringProperty(loginName);
    }

    public StringProperty getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = new SimpleStringProperty(loginPassword);
    }

    public StringProperty getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = new SimpleStringProperty(loginType);
    }

    public BooleanProperty getLoginActive() {
        return loginActive;
    }

    public void setLoginActive(boolean loginActive) {
        this.loginActive = new SimpleBooleanProperty(loginActive);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Employee && this.hashCode() == object.hashCode();
    }
}
