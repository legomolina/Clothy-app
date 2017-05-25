package models;


import javafx.beans.property.*;

import java.util.Calendar;
import java.util.Date;

public class Sale {
    private IntegerProperty id;
    private Employee employee;
    private Client client;
    private Date date;
    private StringProperty dateProperty;
    private StringProperty payment;
    private BooleanProperty checked;

    public Sale(int id, Employee loggedEmployee) {
        this(id, loggedEmployee, new Client(0), Calendar.getInstance().getTime(), "");
    }

    public Sale(int id, Employee employee, Client client, Date date, String payment) {
        this.id = new SimpleIntegerProperty(id);
        this.employee = employee;
        this.client = client;
        this.date = date;
        this.dateProperty = new SimpleStringProperty(date != null ? date.toString() : "");
        this.payment = new SimpleStringProperty(payment);
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public StringProperty dateProperty() {
        return dateProperty;
    }

    public void setDate(Date date) {
        this.date = date;
        this.dateProperty = new SimpleStringProperty(date.toString());
    }

    public String getPayment() {
        return payment.get();
    }

    public StringProperty paymentProperty() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment.set(payment);
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
        return object instanceof Sale && this.id.get() == ((Sale) object).getId();
    }
}
