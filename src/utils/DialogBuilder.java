package utils;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class DialogBuilder {
    private JFXDialog dialog = new JFXDialog();
    private JFXDialogLayout layout = new JFXDialogLayout();

    private StackPane rootElement;
    private JFXDialog.DialogTransition transition;
    private DialogType dialogType;
    private String customStyle;

    public enum DialogType {
        CONFIRM,
        ALERT
    }

    public DialogBuilder(StackPane rootElement) {
        this.dialogType = DialogType.ALERT;
        this.transition = JFXDialog.DialogTransition.CENTER;
        this.customStyle = "custom-dialog-style";
    }

    public DialogBuilder(StackPane rootElement, DialogType dialogType, JFXDialog.DialogTransition transition, String customStyle) {
        this.rootElement = rootElement;
        this.dialogType = dialogType;
        this.transition = transition;
        this.customStyle = customStyle;
    }

    public DialogBuilder setContent(Node... content) {
        for (Node c : content)
            layout.getBody().add(c);

        return this;
    }

    public DialogBuilder setHeader(Node... header) {
        for (Node h : header)
            layout.getHeading().add(h);

        return this;
    }

    public DialogBuilder setAcceptButton(EventHandler<ActionEvent> event) {
        JFXButton okButton = new JFXButton("Aceptar");
        okButton.setPadding(new Insets(7, 10, 7, 10));
        okButton.getStyleClass().add("dialog-accept-button");

        okButton.setOnAction(event);

        layout.getActions().add(okButton);

        return this;
    }

    public DialogBuilder setCancelButton(EventHandler<ActionEvent> event) {
        if (dialogType == DialogType.CONFIRM) {
            JFXButton cancelButton = new JFXButton("Cancelar");
            cancelButton.setPadding(new Insets(7, 10, 7, 10));
            cancelButton.getStyleClass().add("dialog-cancel-button");

            cancelButton.setOnAction(event);

            layout.getActions().add(cancelButton);
        }

        return this;
    }

    public DialogBuilder setOverlayClose(boolean close) {
        dialog.setOverlayClose(close);

        return this;
    }

    public JFXDialog getDialog() {
        return dialog;
    }

    public JFXDialog build() {
        dialog.setDialogContainer(rootElement);
        dialog.setContent(layout);
        dialog.setTransitionType(transition);
        dialog.getStyleClass().add(customStyle);

        return dialog;
    }
}
