package components.clientApp;

import customer.CustomerInfo;
import javafx.application.Platform;
import okhttp3.*;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;

import static util.Constants.CUSTOMER_INFO_REFRESH;
import static util.Constants.GSON_INSTANCE;

public class CustomerInfoRefresher extends TimerTask {
    private ClientAppController customerScreenController;
    private String userName;
    private String lastSeenYaz;


    public CustomerInfoRefresher(ClientAppController customerScreenController, String userName) {
        this.customerScreenController = customerScreenController;
        this.userName = userName;
    }

    @Override
    public void run() {
        String finalUrlInformation = CUSTOMER_INFO_REFRESH;
        HttpClientUtil.runAsync(finalUrlInformation, new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("something went wrong");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){}
                else if (response.code() == 200) {
                    String jsonArrayOfInformation = response.body().string();
                    response.body().close();
                    if (jsonArrayOfInformation == null || jsonArrayOfInformation == "")
                        return;
                    CustomerInfo customerInfo = GSON_INSTANCE.fromJson(jsonArrayOfInformation, CustomerInfo.class);
                    Platform.runLater(() -> {
                        if (customerInfo.getCustomer() != null) {
                            customerScreenController.updateCustomerInfo(customerInfo.getCustomer());
                        }
                        customerScreenController.setYazLabel(customerInfo.getYaz().toString());
                        customerScreenController.handleRewind(customerInfo.getRewind());
                    });

                }
            }
        });
    }
}