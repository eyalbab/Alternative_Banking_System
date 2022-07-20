package components.clientApp;

import customer.Customer;
import javafx.application.Platform;
import loan.Categories;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;

import static util.Constants.*;

public class CategoriesRefresher extends TimerTask {
    private ClientAppController customerScreenController;
    private String userName;


    public CategoriesRefresher(ClientAppController customerScreenController, String userName) {
        this.customerScreenController = customerScreenController;
        this.userName = userName;
    }

    @Override
    public void run() {
        String finalUrlInformation = GET_CATEGORIES;
        HttpClientUtil.runAsync(finalUrlInformation, new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("Couldn't get categories");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    System.out.println("Couldn't get categories");
                else if (response.code() == 200) {
                    String jsonArrayOfInformation = response.body().string();
                    response.body().close();
                    if (jsonArrayOfInformation == null || jsonArrayOfInformation == "")
                        return;
                    Categories allCategories = GSON_INSTANCE.fromJson(jsonArrayOfInformation, Categories.class);
                    Platform.runLater(() -> customerScreenController.updateCategories(allCategories));

                }
            }
        });
    }
}