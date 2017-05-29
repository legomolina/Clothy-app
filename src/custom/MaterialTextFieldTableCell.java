package custom;


import com.jfoenix.controls.JFXTextField;
import controllers.database.ArticlesMethods;
import controllers.database.EmployeesMethods;
import custom.validators.CustomRequiredFieldValidator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;
import models.Article;
import models.Employee;

public class MaterialTextFieldTableCell<S, T> extends TableCell<S, T> {
    String ripplerColor;
    private JFXTextField textField;
    private ObjectProperty<StringConverter<T>> converter;

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter(), "#4059A9");
    }

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(String ripperColor) {
        return forTableColumn(new DefaultStringConverter(), ripperColor);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> converter, String ripplerColor) {
        return (list) -> new MaterialTextFieldTableCell(converter, ripplerColor);
    }

    public MaterialTextFieldTableCell() {
        this((StringConverter) null, "#4059A9");
    }

    public MaterialTextFieldTableCell(StringConverter<T> converter, String ripplerColor) {
        this.converter = new SimpleObjectProperty(this, "converter");
        this.getStyleClass().add("text-field-table-cell");
        this.setConverter(converter);
        this.ripplerColor = ripplerColor;
    }

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return this.converter;
    }

    public final void setConverter(StringConverter<T> value) {
        this.converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return (StringConverter) this.converterProperty().get();
    }

    public void startEdit() {
        if (this.isEditable() && this.getTableView().isEditable() && this.getTableColumn().isEditable()) {
            super.startEdit();
            if (this.isEditing()) {
                if (this.textField == null) {
                    this.textField = createTextField(this, this.getConverter());
                }

                startEdit(this, this.getConverter(), (HBox) null, (Node) null, this.textField);
            }

        }
    }

    public void cancelEdit() {
        super.cancelEdit();
        cancelEdit(this, this.getConverter(), (Node) null);
    }

    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, this.getConverter(), (HBox) null, (Node) null, this.textField);
    }

    public void setRipperColor(String color) {
        ripplerColor = color;
    }

    private <T> void updateItem(Cell<T> cell, StringConverter<T> converter, HBox hbox, Node graphic, TextField textField) {
        if (cell.isEmpty()) {
            cell.setText((String) null);
            cell.setGraphic((Node) null);
        } else if (cell.isEditing()) {
            if (textField != null) {
                textField.setText(getItemText(cell, converter));
            }

            cell.setText((String) null);
            if (graphic != null) {
                hbox.getChildren().setAll(new Node[]{graphic, textField});
                cell.setGraphic(hbox);
            } else {
                cell.setGraphic(textField);
            }
        } else {
            cell.setText(getItemText(cell, converter));
            cell.setGraphic(graphic);
        }
    }

    protected <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ? (cell.getItem() == null ? "" : cell.getItem().toString()) : converter.toString(cell.getItem());
    }

    private <T> void cancelEdit(Cell<T> cell, StringConverter<T> converter, Node graphic) {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
    }

    <T> void startEdit(Cell<T> cell, StringConverter<T> converter, HBox hbox, Node graphic, TextField textField) {
        if (textField != null) {
            textField.setText(getItemText(cell, converter));
        }

        cell.setText((String) null);
        if (graphic != null) {
            hbox.getChildren().setAll(new Node[]{graphic, textField});
            cell.setGraphic(hbox);
        } else {
            cell.setGraphic(textField);
        }

        textField.selectAll();
        textField.requestFocus();
    }

    <T> JFXTextField createTextField(Cell<T> cell, StringConverter<T> converter) {
        JFXTextField textField = new JFXTextField(getItemText(cell, converter));

        textField.setOnAction((event) -> {
            if (converter == null) {
                throw new IllegalStateException("Attempting to convert text input into Object, but provided StringConverter is null. Be sure to set a StringConverter in your cell factory.");
            } else {
                try {
                    cell.commitEdit(converter.fromString(textField.getText()));
                } catch (Exception e) {
                    if (converter instanceof NumberStringConverter)
                        cell.commitEdit(converter.fromString("0"));
                }
                event.consume();
            }
        });

        textField.setOnKeyReleased((t) -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cell.cancelEdit();
                t.consume();
            }

        });

        textField.setStyle("-fx-ripper-fill: " + ripplerColor);

        return textField;
    }
}
