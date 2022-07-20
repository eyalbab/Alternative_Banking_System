package components.payment;

import components.clientApp.ClientAppController;
import customer.Customer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import loan.Loan;
import loan.Loans;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;

import static util.Constants.GSON_INSTANCE;

public class PaymentController {

    @FXML
    private ListView notificationList;
    @FXML
    private TableColumn<Loan, String> payLoanId;
    @FXML
    private TableColumn<Loan, String> payLoanCategory;
    @FXML
    private TableColumn<Loan, Integer> payLoanAmount;
    @FXML
    private TableColumn<Loan, Integer> payLoanInterest;
    @FXML
    private TableColumn<Loan, Loan.LoanStatus> payLoanStatus;
    @FXML
    private TableView<Loan> paymentTable;
    @FXML
    private Text statusText;
    @FXML
    private Text currentBalance;
    @FXML
    private Button paymentBtn;
    @FXML
    private Button closeLoanBtn;
    @FXML
    private TextField amountTF;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private ObservableList<String> notificationData = FXCollections.observableArrayList();
    private ObservableList<Loan> data = FXCollections.observableArrayList();
    private ClientAppController custController;

    @FXML
    public void initialize() {
        payLoanId.setCellValueFactory(new PropertyValueFactory<>("id"));
        payLoanCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        payLoanAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        payLoanInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        payLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(Customer.loanInfoForCustomer(curLoan));
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        paymentTable.getColumns().add(expander);

    }

    public void addActiveLoanDetails() {
        data.clear();
        data = FXCollections.observableArrayList(custController.getCurrentCustomer().getPaymentActiveLoans());
        paymentTable.setEditable(true);
        paymentTable.setItems(data);
        currentBalance.setText(String.valueOf(custController.getCurrentCustomer().getBalance()));
    }

    public void setMainController(ClientAppController custController) {
        this.custController = custController;
    }

    public void updateNotifications() {
        notificationData.clear();
        notificationData = FXCollections.observableArrayList(custController.getCurrentCustomer().getNotifications());
        notificationList.setItems(notificationData);
    }

    public void addPaymentDetails() {
        currentBalance.setText(custController.getCurrentCustomer().getBalance().toString());
        if(!paymentTable.isFocused()) {
            addActiveLoanDetails();
        }
        updateNotifications();
    }

    private FXMLLoader loadController(String path) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        return loader;
    }

    @FXML
    String showPopUp() throws IOException {
        FXMLLoader amountLoader = loadController("amountInRisk.fxml");
        amountLoader.setController(this);
        root = amountLoader.load();
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return amountTF.getText();
    }

    @FXML
    void onSubmitAmount(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void handlePayment() throws IOException {
        Loan selectedItem = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            statusText.setText("Need to select loan from table");
            return;
        } else {
            Double amount = selectedItem.getNextPaymentSum();
            if (selectedItem.getStatus().equals(Loan.LoanStatus.RISK)) {
                String res = showPopUp();
                try {
                    amount = Double.valueOf(res);
                } catch (NumberFormatException e) {
                    amount = Double.MAX_VALUE;
                }
            }
            String finalUrl = HttpUrl
                    .parse(Constants.PAYMENT)
                    .newBuilder()
                    .addQueryParameter("amount", amount.toString())
                    .addQueryParameter("loanName", selectedItem.getId())
                    .addQueryParameter("close", "false")
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    statusText.setText("something went wrong");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    response.body().close();
                    if (response.code() == 200) {
                        Platform.runLater(() -> {
                                    addPaymentDetails();
                                    currentBalance.setText(responseBody);
                                    statusText.setText("Payment succeeded");
                                }
                        );
                    } else {
                        Platform.runLater(() -> statusText.setText(responseBody)
                        );
                    }
                }
            });
        }
    }


    public void handleCloseLoan(ActionEvent actionEvent) {
        Loan selectedItem = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            statusText.setText("Need to select loan from table");
        } else {
            Double amount = selectedItem.getTotalPay() - selectedItem.getTotalPaid();
            if (amount > custController.getCurrentCustomer().getBalance()) {
                statusText.setText("Not enough balance");
            } else {
                String finalUrl = HttpUrl
                        .parse(Constants.PAYMENT)
                        .newBuilder()
                        .addQueryParameter("amount", amount.toString())
                        .addQueryParameter("loanName", selectedItem.getId())
                        .addQueryParameter("close", "true")
                        .build()
                        .toString();

                HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        statusText.setText("something went wrong");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        response.body().close();
                        if (response.code() == 200) {
                            Platform.runLater(() -> {
                                        addPaymentDetails();
                                        currentBalance.setText(responseBody);
                                        statusText.setText("Payment succeeded");
                                    }
                            );
                        } else {
                            Platform.runLater(() -> statusText.setText(responseBody)
                            );
                        }
                    }
                });
            }
        }
    }

    public void handleRewind(Boolean isRewind) {
        closeLoanBtn.setDisable(isRewind);
        paymentBtn.setDisable(isRewind);
    }
}
