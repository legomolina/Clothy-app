package models;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class Model {

    private IntegerProperty id;
    private BooleanProperty checked; //Works with table selection checkboxes

    public Model(int id) {
        this.id = new SimpleIntegerProperty(id);
        this.checked = new SimpleBooleanProperty(false);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Model && this.id.get() == ((Model) object).getId();
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

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }
}
