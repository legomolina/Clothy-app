package models;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Map;

public class Article {
    private IntegerProperty id;
    private StringProperty code;
    private StringProperty name;
    private StringProperty description;
    private Brand brand;
    private FloatProperty price;
    private BooleanProperty checked;

    public Article(int id) {
        this(id, "", "", "", null, 0.0f);
    }

    public Article(int id, String code, String name, String description, Brand brand, float price) {
        this.id = new SimpleIntegerProperty(id);
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.brand = brand;
        this.price = new SimpleFloatProperty(price);
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

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public float getPrice() {
        return price.get();
    }

    public FloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
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
        return object instanceof Article && this.id.get() == ((Article) object).getId();
    }
}
