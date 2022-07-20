package components.main;

import components.adminApp.AdminController;
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

public class AdminClient extends Application {

    private AdminController adminAppMainController;
    private LoginPageController loginPageController;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setMinHeight(340);
        primaryStage.setMinWidth(290);
        primaryStage.setTitle("Admin Login");

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
        adminAppMainController.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
