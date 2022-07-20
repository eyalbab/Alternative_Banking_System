package components.transactionTable;

import components.clientApp.ClientAppController;
import customer.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.controlsfx.control.table.TableRowExpanderColumn;

public class TransactionTableController {

    @FXML private TableView<Transaction> TransactionTable;
    @FXML private TableColumn<Transaction, Character> tranIncome;
    @FXML private TableColumn<Transaction, Integer> tranYaz;
    @FXML private TableColumn<Transaction, Double> tranAmount;
    @FXML private TableColumn<Transaction, Double> tranPrevBalance;
    @FXML private TableColumn<Transaction, Double> tranAfterBalance;

    private ObservableList<Transaction> data = FXCollections.observableArrayList();
    private ClientAppController custController;

    @FXML
    public void initialize() {
        tranIncome.setCellValueFactory(new PropertyValueFactory<>("income"));
        tranYaz.setCellValueFactory(new PropertyValueFactory<>("yazDate"));
        tranAmount.setCellValueFactory(new PropertyValueFactory<>("sum"));
        tranPrevBalance.setCellValueFactory(new PropertyValueFactory<>("prevBalance"));
        tranAfterBalance.setCellValueFactory(new PropertyValueFactory<>("afterBalance"));
        TableRowExpanderColumn<Transaction> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Transaction curTran = param.getValue();
            Text details = new Text(curTran.toString());
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        TransactionTable.getColumns().add(expander);
    }

    public void addTransactionsDetails() {
        data.clear();
        data = FXCollections.observableArrayList(custController.getCurrentCustomer().getTransactions());
        TransactionTable.setEditable(true);
        TransactionTable.setItems(data);
    }

    public void setMainController(ClientAppController custController) {
        this.custController = custController;
    }

}
