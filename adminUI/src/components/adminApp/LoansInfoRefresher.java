package components.adminApp;


import customer.Customers;
import javafx.application.Platform;
import loan.Loans;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;

import static util.Constants.*;

public class LoansInfoRefresher extends TimerTask {
    private AdminController adminScreenController;
    private String lastSeenYaz;


    public LoansInfoRefresher(AdminController adminScreenController) {
        this.adminScreenController = adminScreenController;
    }

    @Override
    public void run() {
        String finalUrlInformation = LOANS_LIST;
        HttpClientUtil.runAsync(finalUrlInformation, new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("something went wrong");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    System.out.println("error");
                else if (response.code() == 200) {
                    String jsonLoans = response.body().string();
                    response.body().close();
                    if (jsonLoans == null || jsonLoans == "")
                        return;
                    Loans loansInfo = GSON_INSTANCE.fromJson(jsonLoans, Loans.class);
                    Platform.runLater(() -> {
                        adminScreenController.updateLoansInfo(loansInfo);
                    });

                }
            }
        });
    }
}