package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import controllers.BaseController;
import controllers.database.ArticlesMethods;
import controllers.database.SalesMethods;
import custom.MaterialAutoCompleteTextFieldTableCell;
import custom.MaterialCheckBoxCell;
import custom.MaterialTextFieldTableCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;
import models.Article;
import models.Sale;
import models.SaleLine;
import utils.DialogBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class SaleLinesController {
    private Sale currentSale;
    private BaseController.ActionStatus currentStatus;
    private JFXDialog linesRoot;
    private final ObservableList<SaleLine> lines;
    private int selectedLinesCount;
    private JFXButton deleteSelected;

    private final String RIPPLER_COLOR = "#7856ff";

    private final ChangeListener<Boolean> removeSelectedListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedLinesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedLinesCount > 0);
        }
    };

    SaleLinesController(Sale sale, BaseController.ActionStatus currentStatus) {
        currentSale = sale;
        this.currentStatus = currentStatus;
        selectedLinesCount = 0;

        lines = FXCollections.observableList(SalesMethods.getSaleLines(currentSale));

        for (SaleLine s : lines)
            s.checkedProperty().addListener(removeSelectedListener);
    }

    JFXDialog buildDialog(StackPane rootStackPane) {
        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(createContainer())
                .setOverlayClose(true)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
        this.linesRoot = dialog;

        return dialog;
    }

    private VBox createContainer() {
        TableView<SaleLine> tableView = buildTableView();

        deleteSelected = new JFXButton("Eliminar seleccionados");
        deleteSelected.getStyleClass().add("remove-button");
        deleteSelected.setVisible(false);
        deleteSelected.setOnAction(event -> {
            DialogBuilder dialogBuilder = new DialogBuilder(linesRoot, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
            JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar las líneas seleccionadas?"))
                    .setOverlayClose(false)
                    .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                    .setAcceptButton(actionEvent -> {
                        List<SaleLine> selectedItems = tableView.getSelectionModel().getSelectedItems();

                        SalesMethods.removeSaleLines(selectedItems);
                        lines.removeAll(selectedItems);

                        deleteSelected.setVisible(false);

                        dialogBuilder.getDialog().close();
                    })
                    .build();

            dialog.show();
        });

        VBox container = new VBox();
        container.setSpacing(10.0d);
        container.getChildren().addAll(deleteSelected, tableView);

        return container;
    }

    private TableView<SaleLine> buildTableView() {
        TableView<SaleLine> table = new TableView<>();
        TableColumn<SaleLine, Boolean> lineCheckedTableColumn = new TableColumn<>();
        TableColumn<SaleLine, Number> lineIdTableColumn = new TableColumn<>("Línea venta");
        TableColumn<SaleLine, Number> lineSaleTableColumn = new TableColumn<>("Venta");
        TableColumn<SaleLine, String> lineArticleCodeTableColumn = new TableColumn<>("Código artículo");
        TableColumn<SaleLine, Article> lineArticleNameTableColumn = new TableColumn<>("Nombre artículo");
        TableColumn<SaleLine, Number> lineDiscountTableColumn = new TableColumn<>("Descuento");
        TableColumn<SaleLine, Number> lineQuantityTableColumn = new TableColumn<>("Cantidad");
        TableColumn<SaleLine, Number> lineTotalTableColumn = new TableColumn<>("Total");

        lineCheckedTableColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        lineCheckedTableColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        lineIdTableColumn.setCellValueFactory(param -> param.getValue().idProperty());
        lineSaleTableColumn.setCellValueFactory(param -> param.getValue().getSale().idProperty());
        lineArticleCodeTableColumn.setCellValueFactory(param -> param.getValue().getArticle().codeProperty());
        lineArticleNameTableColumn.setCellValueFactory(param -> param.getValue().articleProperty());
        lineDiscountTableColumn.setCellValueFactory(param -> param.getValue().discountProperty());
        lineQuantityTableColumn.setCellValueFactory(param -> param.getValue().quantityProperty());
        lineTotalTableColumn.setCellValueFactory(param -> param.getValue().totalProperty());

        lineArticleNameTableColumn.setCellFactory(new Callback<TableColumn<SaleLine, Article>, TableCell<SaleLine, Article>>() {
            @Override
            public TableCell<SaleLine, Article> call(TableColumn<SaleLine, Article> saleLineArticleTableColumn) {
                return new TableCell<SaleLine, Article>() {
                    @Override
                    protected void updateItem(Article item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty)
                            setText(item.getName());
                        else
                            setText(null);
                    }
                };
            }
        });

        lineArticleCodeTableColumn.setCellFactory(saleLineStringTableColumn -> {
            MaterialAutoCompleteTextFieldTableCell autoCompleteTextField = new MaterialAutoCompleteTextFieldTableCell(new DefaultStringConverter(), RIPPLER_COLOR);
            autoCompleteTextField.getEntries().addAll(ArticlesMethods.getAllArticles().stream().map(Article::getCode).collect(Collectors.toList()));
            return autoCompleteTextField;
        });
        lineDiscountTableColumn.setCellFactory(MaterialTextFieldTableCell.forTableColumn(new NumberStringConverter(), RIPPLER_COLOR));
        lineQuantityTableColumn.setCellFactory(MaterialTextFieldTableCell.forTableColumn(new NumberStringConverter(), RIPPLER_COLOR));
        lineTotalTableColumn.setCellFactory(new Callback<TableColumn<SaleLine, Number>, TableCell<SaleLine, Number>>() {
            @Override
            public TableCell<SaleLine, Number> call(TableColumn<SaleLine, Number> saleLineNumberTableColumn) {
                return new TableCell<SaleLine, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty)
                            setText(String.valueOf(Math.round(item.floatValue() * 100) / 100));
                    }
                };
            }
        });
        lineTotalTableColumn.setCellFactory(new Callback<TableColumn<SaleLine, Number>, TableCell<SaleLine, Number>>() {
            @Override
            public TableCell<SaleLine, Number> call(TableColumn<SaleLine, Number> saleLineNumberTableColumn) {
                return new TableCell<SaleLine, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty)
                            setText(String.valueOf(item) + " €");
                        else
                            setText(null);
                    }
                };
            }
        });

        lineArticleCodeTableColumn.setOnEditCommit(item -> {
            SaleLine newSale = item.getRowValue();
            System.out.println(lines.get(lines.indexOf(newSale)).getArticle().getName());
            //newSale.setArticle(ArticlesMethods.getArticlesByCode(item.getNewValue()).get(0));
            lines.get(lines.indexOf(newSale)).setArticle(ArticlesMethods.getArticlesByCode(item.getNewValue()).get(0));
            System.out.println(lines.get(lines.indexOf(newSale)).getArticle().getName());
            /*lines.set(lines.indexOf(newSale), newSale);*/
        });
        lineDiscountTableColumn.setOnEditCommit(item -> {
            SaleLine newSale = item.getRowValue();
            newSale.setDiscount(item.getNewValue().floatValue());
            lines.set(lines.indexOf(newSale), newSale);
        });
        lineQuantityTableColumn.setOnEditCommit(item -> {
            SaleLine newSale = item.getRowValue();
            newSale.setQuantity(item.getNewValue().intValue());
            lines.set(lines.indexOf(newSale), newSale);
        });

        lineCheckedTableColumn.setEditable(true);
        lineCheckedTableColumn.setResizable(false);
        lineCheckedTableColumn.setPrefWidth(50);

        lineIdTableColumn.setEditable(false);
        lineIdTableColumn.setResizable(false);
        lineIdTableColumn.setPrefWidth(100);

        lineSaleTableColumn.setEditable(false);
        lineSaleTableColumn.setResizable(false);
        lineSaleTableColumn.setPrefWidth(70);

        lineArticleCodeTableColumn.setEditable(currentStatus != BaseController.ActionStatus.STATUS_VIEWING);
        lineArticleCodeTableColumn.setResizable(false);
        lineArticleCodeTableColumn.setPrefWidth(100);

        lineArticleNameTableColumn.setEditable(false);
        lineArticleNameTableColumn.setResizable(false);
        lineArticleNameTableColumn.setPrefWidth(130);

        lineDiscountTableColumn.setEditable(currentStatus != BaseController.ActionStatus.STATUS_VIEWING);
        lineDiscountTableColumn.setResizable(false);
        lineDiscountTableColumn.setPrefWidth(70);

        lineQuantityTableColumn.setEditable(currentStatus != BaseController.ActionStatus.STATUS_VIEWING);
        lineQuantityTableColumn.setResizable(false);
        lineQuantityTableColumn.setPrefWidth(70);

        lineTotalTableColumn.setEditable(false);
        lineTotalTableColumn.setResizable(false);
        lineTotalTableColumn.setPrefWidth(120);

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (SaleLine s : lines)
                s.setChecked(newValue);
        });
        lineCheckedTableColumn.setGraphic(selectAllCheckbox);

        table.getColumns().addAll(lineCheckedTableColumn, lineIdTableColumn, lineSaleTableColumn,
                lineArticleCodeTableColumn, lineArticleNameTableColumn, lineDiscountTableColumn, lineQuantityTableColumn,
                lineTotalTableColumn);

        table.setPlaceholder(new Label("No hay artículos registrados"));
        table.setEditable(true);
        table.getStyleClass().addAll("sales-table", "line-sale-table");
        table.setItems(lines);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        return table;
    }
}
