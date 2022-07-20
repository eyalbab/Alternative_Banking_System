package components.allCustomersTable;

import components.adminApp.AdminController;
import customer.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AllCustomersTableController {

    @FXML private TableView<Customer> customerTable;

    @FXML private TableColumn<Customer, String> custName;

    @FXML private TableColumn<Customer, Integer> custBalance;

    @FXML private TableColumn<Customer, Integer> custBorrowed;

    @FXML private TableColumn<Customer, Integer> borrowedNew;

    @FXML private TableColumn<Customer, Integer> borrowedPending;

    @FXML private TableColumn<Customer, Integer> borrowedActive;

    @FXML private TableColumn<Customer, Integer> borrowedRisk;

    @FXML private TableColumn<Customer, Integer> borrowedFinished;

    @FXML private TableColumn<Customer, Integer> custLended;

    @FXML private TableColumn<Customer, Integer> lendedNew;

    @FXML private TableColumn<Customer, Integer> lendedPending;

    @FXML private TableColumn<Customer, Integer> lendedActive;

    @FXML private TableColumn<Customer, Integer> lendedRisk;

    @FXML private TableColumn<Customer, Integer> lendedFinished;

    private ObservableList<Customer> data = FXCollections.observableArrayList();
    private AdminController adminController;

    @FXML
    public void initialize() {
        custName.setCellValueFactory(new PropertyValueFactory<>("name"));
        custBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        borrowedNew.setCellValueFactory(new PropertyValueFactory<>("numOfNewBorrowed"));
        borrowedPending.setCellValueFactory(new PropertyValueFactory<>("numOfPendingBorrowed"));
        borrowedActive.setCellValueFactory(new PropertyValueFactory<>("numOfActiveBorrowed"));
        borrowedRisk.setCellValueFactory(new PropertyValueFactory<>("numOfRiskBorrowed"));
        borrowedFinished.setCellValueFactory(new PropertyValueFactory<>("numOfFinishedBorrowed"));
        lendedNew.setCellValueFactory(new PropertyValueFactory<>("numOfNewLended"));
        lendedPending.setCellValueFactory(new PropertyValueFactory<>("numOfPendingLended"));
        lendedActive.setCellValueFactory(new PropertyValueFactory<>("numOfActiveLended"));
        lendedRisk.setCellValueFactory(new PropertyValueFactory<>("numOfRiskLended"));
        lendedFinished.setCellValueFactory(new PropertyValueFactory<>("numOfFinishedLended"));

    }

    public void addCustDetails() {
        data.clear();
        data =  FXCollections.observableArrayList(adminController.getMySystem().getAllCustomers().getAllCustomers());
        customerTable.setEditable(true);
        customerTable.setItems(data);
    }

    public void setMainController(AdminController adminController) {
        this.adminController = adminController;

    }

    public void close() {
    }
}
