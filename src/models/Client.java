package models;


import javafx.beans.property.*;

public class Client {
    private IntegerProperty id;
    private StringProperty nif;
    private StringProperty name;
    private StringProperty surname;
    private StringProperty address;
    private StringProperty email;
    private StringProperty phone;
    private BooleanProperty checked;

    public Client(int id) {
        this(id, "", "", "", "", "", "");
    }

    public Client(int id, String nif, String name, String surname, String address, String email, String phone) {
        this.id = new SimpleIntegerProperty(id);
        this.checked = new SimpleBooleanProperty(false);

        this.nif = new SimpleStringProperty(nif);
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.address = new SimpleStringProperty(address);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
    }

    public String getCompleteName() {
        return this.getName() + " " + this.getSurname();
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

    public String getNif() {
        return nif.get();
    }

    public StringProperty nifProperty() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif.set(nif);
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
        return object instanceof Client && this.id.get() == ((Client) object).getId();
    }
}
