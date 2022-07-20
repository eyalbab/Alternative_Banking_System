package components.adminApp;


import customer.Customers;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;

import static util.Constants.*;

public class CustomersInfoRefresher extends TimerTask {
    private AdminController adminScreenController;

    public CustomersInfoRefresher(AdminController adminScreenController) {
        this.adminScreenController = adminScreenController;
    }

    @Override
    public void run() {
        String finalUrlInformation = CUSTOMERS_LIST;
        HttpClientUtil.runAsync(finalUrlInformation, new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("something went wrong");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    System.out.println("error");
                else if (response.code() == 200) {
                    String jsonCustomers = response.body().string();
                    response.body().close();
                    if (jsonCustomers == null || jsonCustomers == "")
                        return;
                    Customers customersInfo = GSON_INSTANCE.fromJson(jsonCustomers, Customers.class);
                    Platform.runLater(() -> {
                        adminScreenController.updateCustomersInfo(customersInfo);
                    });

                }
            }
        });
    }
}