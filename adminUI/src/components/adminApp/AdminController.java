package components.adminApp;

import components.allCustomersTable.AllCustomersTableController;
import components.allLoansTable.AllLoansTableController;
import customer.Customer;
import customer.Customers;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import loan.Loan;
import loan.Loans;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import system.MySystem;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;

import static util.Constants.*;

public class AdminController {

    @FXML
    private Button increaseYazBtn;

    @FXML
    private Button minusTLBtn;

    @FXML
    private Button plusTLBtn;

    @FXML
    private TableView allLoansTable;

    @FXML
    private TableView allCustomersTable;

    @FXML
    private AllLoansTableController allLoansTableController;

    @FXML
    private AllCustomersTableController allCustomersTableController;

    @FXML
    private Slider timelineSlider;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private MySystem mySystem;
    private ObservableList<String> viewByOptions = FXCollections.observableArrayList();

    public AdminController() {
    }

    public AdminController(MySystem mySystem) {
        this.mySystem = mySystem;
    }

    @FXML
    public void initialize() {
        if (allLoansTableController != null && allCustomersTableController != null) {
            allLoansTableController.setMainController(this);
            allCustomersTableController.setMainController(this);
        }
//        timelineSlider.valueProperty().addListener((obs, oldval, newVal) ->
//                timelineSlider.setValue(Math.round(newVal.doubleValue())));
//        timelineSlider.valueProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                timelineMoved(oldValue.intValue());
//            }
//        });
    }

    private void showPopUp() throws IOException {
        root = FXMLLoader.load(getClass().getResource("pop-up.fxml"));
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void timelineMoved() {
        Double yazToMoveD = timelineSlider.getValue();
        Integer yazToMove = yazToMoveD.intValue();
        Boolean isPresent = timelineSlider.getValue() == timelineSlider.getMax();
        String finalUrl = HttpUrl
                .parse(REWIND_TIMELINE)
                .newBuilder()
                .addQueryParameter("toYaz", yazToMove.toString())
                .addQueryParameter("isPresent", isPresent.toString())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("something went wrong in rewind - failure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful())
                    System.out.println("something went wrong in rewind - response");
            }
        });
    }

    @FXML
    public void advanceInTL() {
        if (timelineSlider.getValue() < timelineSlider.getMax()) {
            timelineSlider.setValue(timelineSlider.getValue() + 1);
            timelineMoved();
        }
    }

    @FXML
    public void decreaseInTL() {
        if (timelineSlider.getValue() > timelineSlider.getMin()) {
            if (timelineSlider.getValue() == timelineSlider.getMax()) {
                saveState();
            }
            System.out.println("value was: " + timelineSlider.getValue() +
                    " now going to be " + (timelineSlider.getValue() - 1));
            timelineSlider.setValue(timelineSlider.getValue() - 1);
            timelineMoved();
        }
    }

    public void saveState() {
        Double yazToMoveD = timelineSlider.getValue();
        Integer yazToMove = yazToMoveD.intValue();
        System.out.println("saving state of " + yazToMove);
        String finalUrl = HttpUrl
                .parse(SAVE_STATE)
                .newBuilder()
                .addQueryParameter("yazToSave", yazToMove.toString())
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("something went wrong in save state-failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    System.out.println("something went wrong in save state-response");
                else {
                }
            }
        });
    }

    @FXML
    public void increaseYaz() {
        if (timelineSlider.getValue() != timelineSlider.getMax())
            return;
        Double yazToMoveD = timelineSlider.getValue();
        Integer yazToMove = yazToMoveD.intValue();
        System.out.println("saving state of " + yazToMove);
        String finalUrl = HttpUrl
                .parse(SAVE_STATE)
                .newBuilder()
                .addQueryParameter("yazToSave", yazToMove.toString())
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("something went wrong in save state-failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    System.out.println("something went wrong in save state-response");
                else {
                    System.out.println("calling continue timeline servlet");
                    HttpClientUtil.runAsync(CONTINUE_TIMELINE, new Callback() {
                        public void onFailure(Call call, IOException e) {
                            System.out.println("something went wrong in continue timeline-failure");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful())
                                System.out.println("something went wrong in continue timeline-response");
                            else {
                                Platform.runLater(() -> {
                                    System.out.println("max was: " + timelineSlider.getMax() +
                                            " now going to be " + (timelineSlider.getMax() + 1));
                                    System.out.println("value was: " + timelineSlider.getValue() +
                                            " now going to be " + (timelineSlider.getValue() + 1));
                                    timelineSlider.setMax(timelineSlider.getMax() + 1);
                                    timelineSlider.setValue(timelineSlider.getValue() + 1);
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    private void afterFileLoaded(String filePathString) {
        allLoansTableController.addLoanDetails();
        allCustomersTableController.addCustDetails();
        increaseYazBtn.setDisable(false);
    }

    public void updateAfterChanges() {
        allLoansTableController.addLoanDetails();
        allCustomersTableController.addCustDetails();
    }

    public MySystem getMySystem() {
        return mySystem;
    }

    public void close() {
        allLoansTableController.close();
        allCustomersTableController.close();
    }

    public void updateCustomersInfo(Customers customersInfo) {
        if (!allCustomersTable.isFocused()) {
            allCustomersTable.getItems().clear();
            ObservableList<Customer> customers = FXCollections.observableArrayList(customersInfo.getAllCustomers());
            allCustomersTable.setItems(customers);
        }
    }

    public void updateLoansInfo(Loans loansInfo) {
        if (!allLoansTable.isFocused()) {
            allLoansTable.getItems().clear();
            ObservableList<Loan> loans = FXCollections.observableArrayList(loansInfo.getAllLoans());
            allLoansTable.setItems(loans);
        }
    }

    public void startInfoRefresh() {
        CustomersInfoRefresher customersRefresher = new CustomersInfoRefresher(this);
        LoansInfoRefresher loansRefresher = new LoansInfoRefresher(this);
        Timer timer1 = new Timer();
        Timer timer2 = new Timer();
        timer1.schedule(customersRefresher, REFRESH_RATE, REFRESH_RATE);
        timer2.schedule(loansRefresher, REFRESH_RATE, REFRESH_RATE);
    }
}

