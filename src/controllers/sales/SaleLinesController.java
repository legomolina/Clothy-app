package controllers.sales;

import com.jfoenix.controls.JFXDialog;
import controllers.BaseController;
import controllers.database.SalesMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import models.Sale;
import models.SaleLine;
import utils.DialogBuilder;

/*public class SaleLinesController {
    private final Sale currentSale;
    private final ObservableList<SaleLine> lines;

    public SaleLinesController(Sale sale) {
        currentSale = sale;

        lines = FXCollections.observableList(SalesMethods.getSaleLines(currentSale));
    }

    public JFXDialog buildDialog(StackPane rootStackPane) {
        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres cancelar la edición?"))
                .setOverlayClose(true)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    if (currentStatus == BaseController.ActionStatus.STATUS_EDITING) {
                        setSaleInformation(selectedSale);
                        currentStatus = BaseController.ActionStatus.STATUS_VIEWING;
                    } else if (currentStatus == BaseController.ActionStatus.STATUS_ADDING) {
                        setInformationLabelsPlaceholder();
                        currentStatus = BaseController.ActionStatus.STATUS_NONE;
                    }

                    showInformationLabels(true);
                    showModificationInputs(false);
                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    public TableView<SaleLine> buildTableView() {
        TableView<SaleLine> table = new TableView<>(lines);
        TableColumn<SaleLine, >
    }
}*/
