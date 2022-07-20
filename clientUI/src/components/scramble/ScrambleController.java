package components.scramble;

import com.google.gson.reflect.TypeToken;
import components.clientApp.ClientAppController;
import customer.Customer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import loan.Categories;
import loan.Loan;

import loan.Loans;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import utils.AbsException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static util.Constants.GSON_INSTANCE;

public class ScrambleController {

    @FXML
    private TextField amountTF;
    @FXML
    private TextField interestTF;
    @FXML
    private TextField yazTF;
    @FXML
    private TextField ownerLoansTF;
    @FXML
    private CheckComboBox<String> categoriesCB;
    @FXML
    private TextField ownershipPercentTF;
    @FXML
    private CheckComboBox<String> filteredLoansCB;
    @FXML
    private Button findLoansBtn;
    @FXML
    private Button investLoansBtn;
    @FXML
    private TableColumn<Loan, String> scrambleLoanId;
    @FXML
    private TableColumn<Loan, String> scrambleLoanCategory;
    @FXML
    private TableColumn<Loan, Integer> scrambleLoanAmount;
    @FXML
    private TableColumn<Loan, Integer> scrambleLoanInterest;
    @FXML
    private TableColumn<Loan, Loan.LoanStatus> scrambleLoanStatus;
    @FXML
    private TableView<Loan> relevantLoansTable;
    @FXML
    private Label statusLabel;
    @FXML
    private Label assignStatusLabel;
    @FXML
    private ProgressBar loadProgress;

    private ObservableList<Loan> data = FXCollections.observableArrayList();
    private ClientAppController custController;

    @FXML
    public void initialize() {
        scrambleLoanId.setCellValueFactory(new PropertyValueFactory<>("id"));
        scrambleLoanCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        scrambleLoanAmount.setCellValueFactory(new PropertyValueFactory<>("capital"));
        scrambleLoanInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        scrambleLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableRowExpanderColumn<Loan> expander = new TableRowExpanderColumn<>(param -> {
            HBox editor = new HBox(20);
            Loan curLoan = param.getValue();
            Text details = new Text(Customer.loanInfoForCustomer(curLoan));
            editor.getChildren().add(details);
            editor.setPrefHeight(Region.USE_COMPUTED_SIZE);
            editor.setPrefWidth(Region.USE_COMPUTED_SIZE);
            return editor;
        });
        relevantLoansTable.getColumns().add(expander);

    }

    public void initScramble() {
        amountTF.clear();
        interestTF.clear();
        yazTF.clear();
        ownerLoansTF.clear();
        data.clear();
        statusLabel.setText("");
        assignStatusLabel.setText("");
        relevantLoansTable.setItems(data);
        categoriesCB.getItems().clear();
        filteredLoansCB.getCheckModel().clearChecks();
        filteredLoansCB.getItems().clear();
        if (custController.getCategories() != null)
            categoriesCB.getItems().addAll(custController.getCategories());
        if (categoriesCB.getItems().size() > 0) {
            categoriesCB.getCheckModel().checkAll();
        }
    }

    public void addFilteredLoanDetails(List<Loan> filtered) {
        data.clear();
        data = FXCollections.observableArrayList(filtered);
        relevantLoansTable.setEditable(true);
        relevantLoansTable.setItems(data);
        filteredLoansCB.getItems().clear();
        for(Loan loan : filtered){
            filteredLoansCB.getItems().add(loan.getId());
        }
    }

    public void setMainController(ClientAppController custController) {
        this.custController = custController;
    }

    @FXML
    void findRelevantLoans(ActionEvent event) {
        String amountToInvest = amountTF.getText();
        String minInterest = interestTF.getText();
        String minTotalYaz = yazTF.getText();
        String maxAliveOwnerLoans = ownerLoansTF.getText();
        Categories chosenCategories = new Categories(categoriesCB.getCheckModel().getCheckedItems());

        boolean clientCheck = (amountToInvest.isEmpty() || chosenCategories.getAllCategories().isEmpty());
        if (clientCheck) {
            statusLabel.setText("Invalid crucial input");
        } else {
            String jsonCategories = GSON_INSTANCE.toJson(chosenCategories);
            String finalUrl = HttpUrl
                    .parse(Constants.FIND_INVEST_LOANS)
                    .newBuilder()
                    .addQueryParameter("amountToInvest", amountToInvest)
                    .addQueryParameter("minInterest", minInterest)
                    .addQueryParameter("minTotalYaz", minTotalYaz)
                    .addQueryParameter("maxAliveOwnerLoans", maxAliveOwnerLoans)
                    .addQueryParameter("jsonCategories", jsonCategories)
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    response.body().close();
                    if (response.code() != 200) {
                        Platform.runLater(() ->
                                statusLabel.setText(responseBody)
                        );
                    } else {
                        Loans foundLoans = GSON_INSTANCE.fromJson(responseBody, Loans.class);
                        Platform.runLater(() -> {
                            addFilteredLoanDetails(foundLoans.getAllLoans());
                        });
                    }
                }
            });
        }
    }

    @FXML
    void assignPickedLoans(ActionEvent event) {
        List<String> chosenLoans = filteredLoansCB.getCheckModel().getCheckedItems();
        if (chosenLoans.isEmpty()) {
            assignStatusLabel.setText("Choose loans first");
        } else {
            Type listType = new TypeToken<List<String>>() {}.getType();
            String jsonChosenLoans = GSON_INSTANCE.toJson(chosenLoans, listType);
            String finalUrl = HttpUrl
                    .parse(Constants.ASSIGN_LOANS)
                    .newBuilder()
                    .addQueryParameter("jsonChosenLoans", jsonChosenLoans)
                    .addQueryParameter("sumToInvest", amountTF.getText())
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    assignStatusLabel.setText("Something went wrong");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    response.body().close();
                    if (response.code() != 200) {
                        Platform.runLater(() ->
                                statusLabel.setText(responseBody)
                        );
                    } else {
                        Platform.runLater(() -> {
                            initScramble();
                            statusLabel.setText("Assigned loans successfully");
                        });
                    }
                }
            });
        }
    }

    public void handleRewind(Boolean isRewind) {
        findLoansBtn.setDisable(isRewind);
        investLoansBtn.setDisable(isRewind);
    }
}
//        String sanityCheck = custController.getMySystem().handleLoanAssignmentPicks(
//                custController.getCurrentCustomer(),
//                amountTF.getText(),
//                interestTF.getText(), yazTF.getText(), ownerLoansTF.getText());
//        if (sanityCheck == null) {
//            int countChecked = filteredLoansCB.getCheckModel().getCheckedItems().size();
//            if (countChecked > 0) {
//                List<String> chosenLoanString = filteredLoansCB.getCheckModel().getCheckedItems();
//                List<Loan> chosenLoans = custController.getMySystem().getLoans().getAllLoans().stream()
//                        .filter(loan -> chosenLoanString.contains(loan.getId())).collect(Collectors.toList());
//                custController.getMySystem().assignLoans(custController.getCurrentCustomer(), chosenLoans, Integer.valueOf(amountTF.getText()));
//                custController.updateAfterChanges();
//                assignStatusLabel.setText("Assigned Loans Successfully!");
//            }
//        } else {
//            assignStatusLabel.setText(sanityCheck);
//        }
//    }
//}
//}
