package components.borrowedTable;

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

public class BorrowedTableController {

    @FXML private TableColumn<Loan, String> borrowLoanId;
    @FXML private TableColumn<Loan, String> borrowLoanCategory;
    @FXML private TableColumn<Loan, Integer>borrowLoanAmount;
    @FXML private TableColumn<Loan, Integer>borrowLoanInterest;
    @FXML private TableColumn<Loan, Loan.LoanStatus> borrowLoanStatus;
    @FXML private TableView<Loan> BorrowedTable;

    private ObservableList<Loan> data = FXCollections.observableArrayList();
    private ClientAppController custController;

    @FXML
    public void initialize() {
        borrowLoanId.setCellValueFactory(new PropertyValueFactory<>("id"));
        borrowLoanCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        borrowLoanAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        borrowLoanInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        borrowLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(Customer.loanInfoForCustomer(curLoan));
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        BorrowedTable.getColumns().add(expander);

    }

    public void addBorrowedLoanDetails() {
        data.clear();
        data = FXCollections.observableArrayList(custController.getCurrentCustomer().getBorrowLoans());
        BorrowedTable.setEditable(true);
        BorrowedTable.setItems(data);
    }

    public void setMainController(ClientAppController custController) {
        this.custController = custController;
    }

}
