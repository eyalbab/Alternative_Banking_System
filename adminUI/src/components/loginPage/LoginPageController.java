package components.loginPage;

import components.adminApp.AdminController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;

public class LoginPageController {

    @FXML
    private StackPane loginPane;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private AnchorPane depositMenu;
    @FXML
    private Button loginBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField usernameTF;
    @FXML
    private Label loginStatusLabel;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        loginStatusLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    void OnCancelLogin(ActionEvent event) {
        closeLoginWindow();
    }

    @FXML
    void OnLogin(ActionEvent event) {
        String userName = usernameTF.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty.");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("is_admin", "True")
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    response.body().close();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Status: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        FXMLLoader adminLoader = new FXMLLoader();
                        adminLoader.setLocation(getClass().getResource("/components/adminApp/AdminApp.fxml"));
                        AdminController adminController = new AdminController();
                        adminLoader.setController(adminController);
                        try {
                            root = adminLoader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        closeLoginWindow();
                        adminController.startInfoRefresh();
                        stage = new Stage();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();

                    });
                }
            }
        });

    }

    private void closeLoginWindow() {
        Stage loginStage = (Stage) loginBtn.getScene().getWindow();
        loginStage.close();
    }
}

