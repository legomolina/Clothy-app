package custom;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class AutoCompleteTextField extends JFXTextField {
    protected SortedSet<String> entries;
    private ContextMenu entriesPopup;
    private static int MAX_ENTRIES = 5;
    private static Paint COINCIDENT_COLOR = Color.TOMATO;
    private static String SUGGESTION_STYLE_CLASS = "suggestion-item";

    public AutoCompleteTextField() {
        this("");
    }

    public AutoCompleteTextField(String text) {
        super(text);

        this.entries = new TreeSet<>();
        this.entriesPopup = new ContextMenu();

        setListener();
    }

    private void setListener() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();

            if (enteredText == null || enteredText.isEmpty()) {
                entriesPopup.hide();
            } else {
                List<String> filteredEntries = entries.stream()
                        .filter(e -> e.toLowerCase().contains(enteredText.toLowerCase()))
                        .collect(Collectors.toList());

                if (!filteredEntries.isEmpty()) {
                    populatePopup(filteredEntries, enteredText);

                    if (!entriesPopup.isShowing())
                        try {
                            entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                        } catch (IllegalArgumentException e) {}
                } else {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            entriesPopup.hide();
        });
    }

    private void populatePopup(List<String> searchResult, String searchRequest) {
        List<CustomMenuItem> suggestions = new LinkedList<>();
        int count = Math.min(searchResult.size(), MAX_ENTRIES);

        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label();

            entryLabel.setGraphic(buildTextFlow(result, searchRequest));
            entryLabel.setPrefHeight(15);
            entryLabel.setCursor(Cursor.HAND);
            entryLabel.getStyleClass().add(SUGGESTION_STYLE_CLASS);

            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            suggestions.add(item);

            item.setOnAction(actionEvent -> {
                setText(result);
                positionCaret(result.length());
                entriesPopup.hide();
            });
        }

        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(suggestions);
    }

    protected TextFlow buildTextFlow(String text, String filter) {
        int filterIndex = text.toLowerCase().indexOf(filter.toLowerCase());

        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textAfter = new Text(text.substring(filterIndex + filter.length()));
        Text textFilter = new Text(text.substring(filterIndex, filterIndex + filter.length()));

        textFilter.setFill(COINCIDENT_COLOR);
        return new TextFlow(textBefore, textFilter, textAfter);
    }

    public SortedSet<String> getEntries() {
        return entries;
    }

    public ContextMenu getEntriesPopup() {
        return entriesPopup;
    }
}