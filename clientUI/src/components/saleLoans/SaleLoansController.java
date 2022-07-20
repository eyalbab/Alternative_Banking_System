package components.saleLoans;

import components.clientApp.ClientAppController;
import customer.Customer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import loan.Loan;
import loan.Loans;
import loan.OfferedLoans;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static util.Constants.*;

public class SaleLoansController {

    @FXML
    private TableView<Loan> forSaleLoansTable;
    @FXML
    private TableColumn<Loan, String> forSaleId;
    @FXML
    private TableColumn<Loan, String> forSaleCategory;
    @FXML
    private TableColumn<Loan, Integer> forSaleAmount;
    @FXML
    private TableColumn<Loan, Integer> forSaleInterest;
    @FXML
    private TableColumn<Loan, Loan.LoanStatus> forSaleStatus;

    @FXML
    private TableView<Loan> MyLendedLoansTable;
    @FXML
    private TableColumn<Loan, String> mySaleId;
    @FXML
    private TableColumn<Loan, String> mySaleCategory;
    @FXML
    private TableColumn<Loan, Integer> mySaleAmount;
    @FXML
    private TableColumn<Loan, Integer> mySaleInterest;
    @FXML
    private TableColumn<Loan, Loan.LoanStatus> mySaleStatus;

    @FXML
    private Button offerLoanBtn;
    @FXML
    private Button buyLoanBtn;
    @FXML
    private Text saleStatusLabel;


    private ObservableList<Loan> forSaleData = FXCollections.observableArrayList();
    private ObservableList<Loan> mySaleData = FXCollections.observableArrayList();
    private ClientAppController custController;

    @FXML
    public void initialize() {
        forSaleId.setCellValueFactory(new PropertyValueFactory<>("id"));
        forSaleCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        forSaleAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        forSaleInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        forSaleStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(Customer.loanInfoForCustomer(curLoan));
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        forSaleLoansTable.getColumns().add(expander);

        mySaleId.setCellValueFactory(new PropertyValueFactory<>("id"));
        mySaleCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        mySaleAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        mySaleInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        mySaleStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander2 = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(Customer.loanInfoForCustomer(curLoan));
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        MyLendedLoansTable.getColumns().add(expander2);

    }

    public void updateSaleLoansTab() {
        if (!forSaleLoansTable.isFocused())
            addForSaleLoansTableDetails();
        if (!MyLendedLoansTable.isFocused())
            addMySaleLoansTableDetails();
    }

    private void addForSaleLoansTableDetails() {
        HttpClientUtil.runAsync(FIND_LOANS_FOR_SALE, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.body().close();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            saleStatusLabel.setText("something went wrong")
                    );
                } else {
                    OfferedLoans foundLoans = GSON_INSTANCE.fromJson(responseBody, OfferedLoans.class);
                    if (foundLoans != null) {
                        Platform.runLater(() -> {
                            forSaleData.clear();
                            List<Loan> filteredFoundLoans = foundLoans.getLoansOnly().stream()
                                    .filter(loan -> !(loan.getOwner().equals(custController.getCurrentCustomer().getName())))
                                    .collect(Collectors.toList());
                            forSaleData = FXCollections.observableArrayList(filteredFoundLoans);
                            forSaleLoansTable.setEditable(true);
                            forSaleLoansTable.setItems(forSaleData);
                        });
                    }
                }
            }
        });
    }

    private void addMySaleLoansTableDetails() {
        mySaleData.clear();
        mySaleData = FXCollections.observableArrayList(custController.getCurrentCustomer().getLendingPaymentActiveLoans());
        MyLendedLoansTable.setEditable(true);
        MyLendedLoansTable.setItems(mySaleData);
    }

    public void setMainController(ClientAppController custController) {
        this.custController = custController;
    }

    @FXML
    void buyLoan(ActionEvent event) {
        Loan selectedItem = forSaleLoansTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            saleStatusLabel.setText("Need to select loan from table");
            return;
        }
        String finalUrl = HttpUrl
                .parse(BUY_LOAN)
                .newBuilder()
                .addQueryParameter("loanName", selectedItem.getId())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                saleStatusLabel.setText("something went wrong");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.body().close();
                Platform.runLater(() -> {
                            updateSaleLoansTab();
                            saleStatusLabel.setText(responseBody);
                        }
                );
            }
        });
    }

    @FXML
    void offerLoanForSale(ActionEvent event) {
        Loan selectedItem = MyLendedLoansTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            saleStatusLabel.setText("Need to select loan from table");
            return;
        }
        String finalUrl = HttpUrl
                .parse(Constants.OFFER_LOAN)
                .newBuilder()
                .addQueryParameter("loanName", selectedItem.getId())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                saleStatusLabel.setText("something went wrong");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.body().close();
                Platform.runLater(() -> {
                            updateSaleLoansTab();
                            saleStatusLabel.setText(responseBody);
                        }
                );
            }
        });
    }

    public void handleRewind(Boolean isRewind) {
        buyLoanBtn.setDisable(isRewind);
        offerLoanBtn.setDisable(isRewind);
    }
}




