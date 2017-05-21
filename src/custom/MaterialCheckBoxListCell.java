package custom;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MaterialCheckBoxListCell<T> extends JFXListCell<T> {
    private final JFXCheckBox checkBox;
    private ObservableValue<Boolean> booleanProperty;
    private ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallback;

    public static <T> Callback<ListView<T>, ListCell<T>> forListView(Callback<T, ObservableValue<Boolean>> getSelectedProperty) {
        return (list) -> new MaterialCheckBoxListCell<>(getSelectedProperty);
    }

    public MaterialCheckBoxListCell(Callback<T, ObservableValue<Boolean>> getSelectedProperty) {
        this.selectedStateCallback = new SimpleObjectProperty<>(this, "selectedStateCallback");
        this.getStyleClass().add("check-box-list-cell");
        this.setSelectedStateCallback(getSelectedProperty);
        this.checkBox = new JFXCheckBox();
        this.setAlignment(Pos.CENTER_LEFT);
        this.setContentDisplay(ContentDisplay.LEFT);
        this.setGraphic(null);
    }

    public final ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
        return this.selectedStateCallback;
    }

    public final void setSelectedStateCallback(Callback<T, ObservableValue<Boolean>> value) {
        this.selectedStateCallbackProperty().set(value);
    }

    public final Callback<T, ObservableValue<Boolean>> getSelectedStateCallback() {
        return (Callback)this.selectedStateCallbackProperty().get();
    }

    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty) {
            Callback<T, ObservableValue<Boolean>> callback = this.getSelectedStateCallback();
            if(callback == null) {
                throw new NullPointerException("The CheckBoxListCell selectedStateCallbackProperty can not be null");
            }

            this.setGraphic(this.checkBox);
            this.setText(item == null ? "" : item.toString());
            if(this.booleanProperty != null) {
                this.checkBox.selectedProperty().unbindBidirectional((BooleanProperty)this.booleanProperty);
            }

            this.booleanProperty = (ObservableValue)callback.call(item);
            if(this.booleanProperty != null) {
                this.checkBox.selectedProperty().bindBidirectional((BooleanProperty)this.booleanProperty);
            }
        } else {
            this.setGraphic((Node)null);
            this.setText((String)null);
        }

    }
}
