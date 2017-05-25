package custom;


import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

public class MaterialCheckBoxCell<S, T> extends TableCell<S, T> {
    private JFXCheckBox checkbox;
    private ObservableValue<Boolean> booleanProperty;
    private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallback;

    public MaterialCheckBoxCell() {
        this(Pos.CENTER);
    }

    public MaterialCheckBoxCell(Pos cellPosition) {
        setAlignment(cellPosition);
        setPadding(new Insets(50));

        setGraphic((Node) null);

        getStyleClass().add("material-checkbox-cell");

        checkbox = new JFXCheckBox();
        checkbox.setAlignment(Pos.TOP_LEFT);
        selectedStateCallback = new SimpleObjectProperty(this, "selectedStateCallback");
        this.setSelectedStateCallback(null);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        //If watching item is gone, set cell as empty text
        if (!empty) {
            setGraphic(checkbox);

            if (booleanProperty instanceof BooleanProperty)
                checkbox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);

            ObservableValue<?> value = getSelectedProperty();
            if (value instanceof BooleanProperty) {
                booleanProperty = (BooleanProperty) value;
                checkbox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
            }

            checkbox.disableProperty().bind(Bindings.not(this.getTableView().editableProperty().and(this.getTableColumn().editableProperty()).and(this.editableProperty())));

            TableRow row = getTableRow();

            if (row != null) {
                int rowIndex = row.getIndex();
                SelectionModel selectionModel = getTableView().getSelectionModel();

                if ((Boolean) item)
                    selectionModel.select(rowIndex);
                else
                    selectionModel.clearSelection(rowIndex);
            }
        } else {
            //this throws exception
            this.setGraphic((Node) null);
        }
    }

    private ObservableValue<?> getSelectedProperty() {
        return this.getSelectedStateCallback() != null ? (ObservableValue) this.getSelectedStateCallback().call(Integer.valueOf(this.getIndex())) : this.getTableColumn().getCellObservableValue(this.getIndex());
    }

    private Callback getSelectedStateCallback() {
        return this.selectedStateCallbackProperty().get();
    }

    private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
        return this.selectedStateCallback;
    }

    public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Boolean>> value) {
        this.selectedStateCallbackProperty().set(value);
    }
}
