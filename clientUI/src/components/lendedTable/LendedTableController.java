package components.lendedTable;

import components.clientApp.ClientAppController;
import customer.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import loan.Loan;
import org.controlsfx.control.table.TableRowExpanderColumn;

public class LendedTableController {

    @FXML private TableColumn<Loan, String> lendedLoanId;
    @FXML private TableColumn<Loan, String> lendedLoanCategory;
    @FXML private TableColumn<Loan, Integer>lendedLoanAmount;
    @FXML private TableColumn<Loan, Integer>lendedLoanInterest;
    @FXML private TableColumn<Loan, Loan.LoanStatus> lendedLoanStatus;
    @FXML private TableView<Loan> lendedTable;

    private ObservableList<Loan> data = FXCollections.observableArrayList();
    private ClientAppController custController;

    @FXML
    public void initialize() {
        lendedLoanId.setCellValueFactory(new PropertyValueFactory<>("id"));
        lendedLoanCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        lendedLoanAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        lendedLoanInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        lendedLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(Customer.loanInfoForCustomer(curLoan));
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        lendedTable.getColumns().add(expander);

    }

    public void addLendedLoanDetails() {
        data.clear();
        data = FXCollections.observableArrayList(custController.getCurrentCustomer().getLendingLoans());
        lendedTable.setEditable(true);
        lendedTable.setItems(data);
    }

    public void setMainController(ClientAppController custController) {
        this.custController = custController;

    }

}
