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
    private BooleanProperty checked;

    public Employee(int id) {
        this.id = new SimpleIntegerProperty(id);
        this.checked = new SimpleBooleanProperty(false);
    }

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
        this.checked = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSurname() {
        return surname.get();
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getLoginName() {
        return loginName.get();
    }

    public StringProperty loginNameProperty() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName.set(loginName);
    }

    public String getLoginPassword() {
        return loginPassword.get();
    }

    public StringProperty loginPasswordProperty() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword.set(loginPassword);
    }

    public String getLoginType() {
        return loginType.get();
    }

    public StringProperty loginTypeProperty() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType.set(loginType);
    }

    public boolean isLoginActive() {
        return loginActive.get();
    }

    public BooleanProperty loginActiveProperty() {
        return loginActive;
    }

    public void setLoginActive(boolean loginActive) {
        this.loginActive.set(loginActive);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Employee && this.id.get() == ((Employee) object).getId();
    }
}
