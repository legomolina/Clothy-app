package models;

import javafx.beans.property.*;

public class Size {
    private IntegerProperty id;
    private StringProperty size;
    private BooleanProperty checked;

    public Size(int id) {
        this(id, "");
    }

    public Size(int id, String size) {
        this.id = new SimpleIntegerProperty(id);
        this.checked = new SimpleBooleanProperty(false);

        this.size = new SimpleStringProperty(size);
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

    public String getSize() {
        return size.get();
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
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
        return object instanceof Size && this.id.get() == ((Size) object).getId();
    }
}
