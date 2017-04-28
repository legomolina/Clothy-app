package controllers.main_menu;


import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

public class MainMenuController {
    @FXML
    private Pane titleContainer;
    @FXML
    private Pane usersButton;
    @FXML
    private Pane articlesButton;
    @FXML
    private Pane clientsButton;
    @FXML
    private Pane pedidos;
    @FXML
    private JFXMasonryPane menuButtons;

    public void setInitialText() {
        titleContainer.setEffect(new BoxBlur(5, 5, 3));

        JFXRippler usersRipple = new JFXRippler(usersButton);
        usersRipple.setCursor(Cursor.HAND);
        usersRipple.setRipplerFill(Paint.valueOf("#ffd351"));
        menuButtons.getChildren().add(usersRipple);

        JFXRippler articlesRipple = new JFXRippler(articlesButton);
        articlesRipple.setCursor(Cursor.HAND);
        articlesRipple.setRipplerFill(Paint.valueOf("#e24848"));
        menuButtons.getChildren().add(articlesRipple);

        JFXRippler clientsRipple = new JFXRippler(clientsButton);
        clientsRipple.setCursor(Cursor.HAND);
        clientsRipple.setRipplerFill(Paint.valueOf("#00c8ff"));
        menuButtons.getChildren().add(clientsRipple);

        JFXRippler pedidosRipple = new JFXRippler(pedidos);
        pedidosRipple.setCursor(Cursor.HAND);
        pedidosRipple.setRipplerFill(Paint.valueOf("#76e285"));
        menuButtons.getChildren().add(pedidosRipple);
    }
}
