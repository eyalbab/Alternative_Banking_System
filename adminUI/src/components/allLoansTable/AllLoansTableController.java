package components.allLoansTable;

import components.adminApp.AdminController;
import components.api.HttpStatusUpdate;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.REFRESH_RATE;

public class AllLoansTableController {

    @FXML private TableColumn<Loan, String> loanId;
    @FXML private TableColumn<Loan, String> loanOwner;
    @FXML private TableColumn<Loan, String> loanCategory;
    @FXML private TableColumn<Loan, Integer> loanAmount;
    @FXML private TableColumn<Loan, Integer> loanInterest;
    @FXML private TableColumn<Loan, Loan.LoanStatus> loanStatus;
    @FXML private TableView<Loan> loansTable;

    private ObservableList<Loan> data = FXCollections.observableArrayList();
    private AdminController adminController;
    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private HttpStatusUpdate httpStatusUpdate;

    public AllLoansTableController() {
        autoUpdate = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        loanOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        loanId.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        loanInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        loanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(curLoan.toString());
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        loansTable.getColumns().add(expander);

    }

    public void addLoanDetails() {
        data.clear();
        data =  FXCollections.observableArrayList(adminController.getMySystem().getLoans().getAllLoans());
        loansTable.setEditable(true);
        loansTable.setItems(data);
    }

    public void setMainController(AdminController adminController) {
        this.adminController = adminController;

    }

    private void updateUsersList(List<Loan> allLoans) {
        Platform.runLater(() -> {

            data.clear();
            data = FXCollections.observableArrayList(allLoans);
            loansTable.setEditable(true);
            loansTable.setItems(data);
        });
    }

    public void startListRefresher() {
        listRefresher = new LoansListRefresher(
                autoUpdate,
                httpStatusUpdate::updateHttpLine,
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void close() {
        loansTable.getItems().clear();
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }

}
