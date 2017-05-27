package controllers.sales;

import com.jfoenix.controls.JFXDialog;
import controllers.database.SalesMethods;
import custom.MaterialCheckBoxCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import models.Sale;
import models.SaleLine;
import utils.DialogBuilder;

public class SaleLinesController {
    private final Sale currentSale;
    private final ObservableList<SaleLine> lines;

    public SaleLinesController(Sale sale) {
        currentSale = sale;

        lines = FXCollections.observableList(SalesMethods.getSaleLines(currentSale));

        System.out.println(lines);
    }

    public JFXDialog buildDialog(StackPane rootStackPane) {
        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(buildTableView())
                .setOverlayClose(true)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();

        return dialog;
    }

    public TableView<SaleLine> buildTableView() {
        TableView<SaleLine> table = new TableView<>(lines);
        TableColumn<SaleLine, Boolean> lineCheckedTableColumn = new TableColumn<>();
        TableColumn<SaleLine, Number> lineIdTableColumn = new TableColumn<>("Línea venta");
        TableColumn<SaleLine, Number> lineSaleTableColumn = new TableColumn<>("Venta");
        TableColumn<SaleLine, String> lineArticleCodeTableColumn = new TableColumn<>("Código artículo");
        TableColumn<SaleLine, String> lineArticleNameTableColumn = new TableColumn<>("Nombre artículo");
        TableColumn<SaleLine, Number> lineDiscountTableColumn = new TableColumn<>("Descuento");
        TableColumn<SaleLine, Number> lineQuantityTableColumn = new TableColumn<>("Cantidad");

        lineCheckedTableColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        lineCheckedTableColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        lineIdTableColumn.setCellValueFactory(param -> param.getValue().idProperty());
        lineSaleTableColumn.setCellValueFactory(param -> param.getValue().getSale().idProperty());
        lineArticleCodeTableColumn.setCellValueFactory(param -> param.getValue().getArticle().codeProperty());
        lineArticleNameTableColumn.setCellValueFactory(param -> param.getValue().getArticle().nameProperty());
        lineDiscountTableColumn.setCellValueFactory(param -> param.getValue().discountProperty());
        lineQuantityTableColumn.setCellValueFactory(param -> param.getValue().quantityProperty());

        table.getColumns().addAll(lineCheckedTableColumn, lineIdTableColumn, lineSaleTableColumn,
                lineArticleCodeTableColumn, lineArticleNameTableColumn, lineDiscountTableColumn, lineQuantityTableColumn);

        return table;
    }
}
