package components.clientApp;

import components.borrowedTable.BorrowedTableController;
import components.lendedTable.LendedTableController;
import components.payment.PaymentController;
import components.saleLoans.SaleLoansController;
import components.scramble.ScrambleController;
import customer.Customer;
import customer.Transaction;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import loan.Categories;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import static util.Constants.*;

public class ClientAppController {
    @FXML
    private TableView borrowedTable;
    @FXML
    private TableView lendedTable;
    @FXML
    private LendedTableController lendedTableController;
    @FXML
    private BorrowedTableController borrowedTableController;
    @FXML
    private ScrambleController scrambleController;
    @FXML
    private PaymentController paymentController;
    @FXML
    private SaleLoansController saleLoansController;

    @FXML
    private Label currentYazLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private GridPane scramble;
    @FXML
    private GridPane saleLoans;
    @FXML
    private Text currentBalance;
    @FXML
    private Tab informationTab;
    @FXML
    private Tab scrambleTab;
    @FXML
    private Tab saleLoansTab;
    @FXML
    private Tab paymentTab;
    @FXML
    private TextField amountTF;
    @FXML
    private TextField loanNameTF;
    @FXML
    private TextField capitalTF;
    @FXML
    private TextField totalYazTF;
    @FXML
    private TextField payRateTF;
    @FXML
    private TextField interestTF;
    @FXML
    private ComboBox<String> nlCategoryCB;
    @FXML
    private Text nlStatusText;
    @FXML
    private Text statusText;
    @FXML
    private Text statusLabel;
    @FXML
    private Text inRewindText;
    @FXML
    private Text saleStatusLabel;
    @FXML
    private Button submitBtn;
    @FXML
    private Button newLoanBtn;
    @FXML
    private Button loadFileBtn;
    @FXML
    private Button depositBtn;
    @FXML
    private Button withdrawBtn;
    @FXML
    private ListView transactionsList;
    @FXML
    private ImageView myImage;


    private Customer currentCustomer;
    private Categories currentCategories;
    private Boolean isRewind = false;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private ObservableList<Transaction> tranData = FXCollections.observableArrayList();
    private final StringProperty statusMessageProperty = new SimpleStringProperty();

    public ClientAppController() {
    }

    @FXML
    public void initialize() {
        currentBalance.setText(currentCustomer != null ? currentCustomer.getBalance().toString() : "0.0");
        statusLabel.textProperty().bind(statusMessageProperty);
        if (lendedTableController != null && borrowedTableController != null
                && scrambleController != null && paymentController != null && saleLoansController != null) {
            borrowedTableController.setMainController(this);
            lendedTableController.setMainController(this);
            scrambleController.setMainController(this);
            paymentController.setMainController(this);
            saleLoansController.setMainController(this);
        }
    }

    private void updateTransactions() {
        tranData = FXCollections.observableArrayList(currentCustomer.getTransactions());
        transactionsList.setItems(tranData);
    }

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public void setYazLabel(String yaz) {
        currentYazLabel.setText(yaz);
    }

    public void updateAfterChanges() {
        updateTransactions();
        currentBalance.setText(String.valueOf(currentCustomer.getBalance()));
        if (!borrowedTable.isFocused())
            borrowedTableController.addBorrowedLoanDetails();
        if (!lendedTable.isFocused())
            lendedTableController.addLendedLoanDetails();
        if (!scrambleTab.isSelected()) {
            scrambleController.initScramble();
        }
        paymentController.addPaymentDetails();
        saleLoansController.updateSaleLoansTab();

    }

    public void changeImage(String url) {
        Image toSet = new Image(url);
        myImage.setImage(toSet);
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    @FXML
    void OnTyped(KeyEvent event) {
        submitBtn.setDisable(false);
    }

    @FXML
    void OnCancelDeposit(ActionEvent event) {
        stage.close();
    }

    @FXML
    void OnCancelWithdraw(ActionEvent event) {
        stage.close();
    }

    @FXML
    void OnSubmitDeposit(ActionEvent event) {
        String amount = amountTF.getText();
        if (amount.isEmpty()) {
            statusMessageProperty.set("can't deposit empty");
            stage.close();
            return;
        }
        stage.close();
        String finalUrl = HttpUrl
                .parse(Constants.DEPOSIT)
                .newBuilder()
                .addQueryParameter("amount", amount)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        statusMessageProperty.set("Something went wrong")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.body().close();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            statusMessageProperty.set(responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        statusMessageProperty.set(responseBody);
                    });
                }
            }
        });

    }

    @FXML
    void OnSubmitWithdraw(ActionEvent event) {
        String amount = amountTF.getText();
        if (amount.isEmpty()) {
            statusMessageProperty.set("can't withdraw empty");
            stage.close();
            return;
        }
        stage.close();
        String finalUrl = HttpUrl
                .parse(Constants.WITHDRAW)
                .newBuilder()
                .addQueryParameter("amount", amount)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        statusMessageProperty.set("Something went wrong")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.body().close();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            statusMessageProperty.set(responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        statusMessageProperty.set(responseBody);
                    });
                }
            }
        });
    }

    @FXML
    void OnCancelNewLoan() {
        statusMessageProperty.set("");
        stage.close();
    }

    @FXML
    void OnSubmitNewLoan() {
        boolean clientCheck = (loanNameTF.getText().isEmpty()) ||
                (capitalTF.getText().isEmpty()) ||
                (totalYazTF.getText().isEmpty()) ||
                (payRateTF.getText().isEmpty()) ||
                (interestTF.getText().isEmpty()) ||
                nlCategoryCB.getSelectionModel().isEmpty();
        if (clientCheck) {
            nlStatusText.setText("Invalid Loan Input");
        } else {
            String finalUrl = HttpUrl
                    .parse(Constants.NEW_LOAN)
                    .newBuilder()
                    .addQueryParameter("loanName", loanNameTF.getText())
                    .addQueryParameter("capital", capitalTF.getText())
                    .addQueryParameter("totalYaz", totalYazTF.getText())
                    .addQueryParameter("payRate", payRateTF.getText())
                    .addQueryParameter("interest", interestTF.getText())
                    .addQueryParameter("category", nlCategoryCB.getValue())
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            statusMessageProperty.set("Something went wrong")
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    response.body().close();
                    if (response.code() != 200) {
                        Platform.runLater(() ->
                                nlStatusText.setText(responseBody)
                        );
                    } else {
                        Platform.runLater(() -> {
                            statusMessageProperty.set(responseBody);
                            stage.close();
                        });
                    }
                }
            });
        }
    }


    @FXML
    void withdrawFromCustomer() throws IOException {
        FXMLLoader withdrawLoader = loadController("Withdraw.fxml");
        withdrawLoader.setController(this);
        root = withdrawLoader.load();
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    void depositToCustomer() throws IOException {
        FXMLLoader depositLoader = loadController("Deposit.fxml");
        depositLoader.setController(this);
        root = depositLoader.load();
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private FXMLLoader loadController(String path) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        return loader;
    }

    @FXML
    void popUpLoanForm() throws IOException {
        FXMLLoader nlLoader = loadController("NewLoanForm.fxml");
        nlLoader.setController(this);
        root = nlLoader.load();
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        ObservableList<String> categoriesObservableList =
                FXCollections.observableArrayList(currentCategories.getAllCategories());
        nlCategoryCB.setItems(categoriesObservableList);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    void loadFile() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            HttpClientUtil.runFileUpload(file, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            statusMessageProperty.set("Something went wrong")
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    response.body().close();
                    if (response.code() != 200) {
                        Platform.runLater(() ->
                                statusMessageProperty.set(responseBody)
                        );
                    } else {
                        Platform.runLater(() ->
                                statusMessageProperty.set("loaded file successfully")
                        );
                    }
                }
            });
        }
    }

    @FXML
    void handleImageClicked() {
        Polyline pol = new Polyline();
        pol.getPoints().addAll(new Double[]{
                0.0, 0.0,
                100.0, -50.0,
                200.0, 0.0,
                0.0, 0.0
        });
        PathTransition p = new PathTransition();
        p.setNode(myImage);
        p.setDuration(Duration.seconds(1.5));
        p.setPath(pol);
        p.play();
    }

    void handleDepositAnimation() {
        FadeTransition fade = new FadeTransition();
        fade.setNode(currentBalance);
        fade.setDuration(Duration.seconds(0.7));
        fade.setCycleCount(4);
        fade.setInterpolator(Interpolator.LINEAR);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        ScaleTransition scale = new ScaleTransition();
        scale.setNode(currentBalance);
        scale.setDuration(Duration.seconds(0.7));
        scale.setCycleCount(4);
        scale.setInterpolator(Interpolator.LINEAR);
        scale.setByX(1.5);
        scale.setByY(1.5);
        scale.setAutoReverse(true);
        scale.play();
    }

    public void startInfoRefresh(String customerName) {
        CustomerInfoRefresher customerInfoRefresher = new CustomerInfoRefresher(this, customerName);
        Timer timer = new Timer();
        timer.schedule(customerInfoRefresher, REFRESH_RATE, REFRESH_RATE);
        CategoriesRefresher categoriesRefresher = new CategoriesRefresher(this, customerName);
        Timer timer2 = new Timer();
        timer2.schedule(categoriesRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void updateCustomerInfo(Customer cust) {
        currentCustomer = cust;
        updateAfterChanges();
    }

    public void updateCategories(Categories categories) {
        currentCategories = categories;
        if (currentCustomer != null) {
            updateAfterChanges();
        }
    }

    public void close() {
//        borrowedTableController.close();
//        lendedTableController.close();
//        scrambleController.close();
//        paymentController.close();
    }

    public List<String> getCategories() {
        return currentCategories != null ? currentCategories.getAllCategories() : null;
    }

    public void handleRewind(Boolean isRewind) {
        if (this.isRewind != isRewind) {
            newLoanBtn.setDisable(isRewind);
            loadFileBtn.setDisable(isRewind);
            depositBtn.setDisable(isRewind);
            withdrawBtn.setDisable(isRewind);
            scrambleController.handleRewind(isRewind);
            paymentController.handleRewind(isRewind);
            saleLoansController.handleRewind(isRewind);
            this.isRewind = isRewind;
            inRewindText.setVisible(isRewind);
        }
    }
}
