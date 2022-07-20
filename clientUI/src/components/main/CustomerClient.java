package components.main;

import components.clientApp.ClientAppController;
import components.loginPage.LoginPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static util.Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

public class CustomerClient extends Application {

    private LoginPageController loginPageController;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setMinHeight(340);
        primaryStage.setMinWidth(290);
        primaryStage.setTitle("Customer Login");

        URL loginPage = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            loginPageController = fxmlLoader.getController();

            Scene scene = new Scene(root, 290, 340);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
