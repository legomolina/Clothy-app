package custom;


import controllers.database.ArticlesMethods;
import custom.validators.CustomRequiredFieldValidator;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import models.Article;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MaterialAutoCompleteTextFieldTableCell<S, T> extends MaterialTextFieldTableCell<S, T> {
    private AutoCompleteTextField textField;
    private SortedSet<String> entries;

    public MaterialAutoCompleteTextFieldTableCell(StringConverter<T> converter, String ripplerColor) {
        super(converter, ripplerColor);
        entries = new TreeSet<>();
    }

    public SortedSet<String> getEntries() {
        return entries;
    }

    @Override
    public void startEdit() {
        if (this.isEditable() && this.getTableView().isEditable() && this.getTableColumn().isEditable()) {
            super.startEdit();
            if (this.isEditing()) {
                if (this.textField == null) {
                    this.textField = createAutoCompleteTextField(this, this.getConverter());
                }

                startEdit(this, this.getConverter(), (HBox) null, (Node) null, this.textField);
            }

        }
    }

    private <T> AutoCompleteTextField createAutoCompleteTextField(Cell<T> cell, StringConverter<T> converter) {
        super.createTextField(cell, converter);

        AutoCompleteTextField textField = new AutoCompleteTextField(super.getItemText(cell, converter));
        textField.getEntries().addAll(entries);
        textField.getValidators().add(new ArticleExistsValidator("No existe"));

        textField.setOnAction((event) -> {
            if (converter == null) {
                throw new IllegalStateException("Attempting to convert text input into Object, but provided StringConverter is null. Be sure to set a StringConverter in your cell factory.");
            } else {
                try {
                    if(!textField.validate()) {
                        event.consume();
                        return;
                    }

                    cell.commitEdit(converter.fromString(textField.getText()));
                } catch (Exception e) {
                    if (converter instanceof NumberStringConverter)
                        cell.commitEdit(converter.fromString("0"));
                }
                event.consume();
            }
        });

        return textField;
    }

    class ArticleExistsValidator extends CustomRequiredFieldValidator {
        ArticleExistsValidator(String message) {
            super(message);
        }

        protected void eval() {
            if (this.srcControl.get() instanceof TextInputControl)
                this.evalTextInputField();
        }

        private void evalTextInputField() {
            TextInputControl textField = (TextInputControl) this.srcControl.get();

            if(!textField.getText().isEmpty()) {
                String textFieldArticleCode = textField.getText();
                this.hasErrors.set(!articleExists(textFieldArticleCode));
            }
        }

        private boolean articleExists(String articleCode) {
            List<Article> articles = ArticlesMethods.getArticlesByCode(articleCode);

            return !articles.isEmpty();
        }
    }
}
