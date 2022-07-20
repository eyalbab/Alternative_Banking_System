package components.allLoansTable;

import javafx.beans.property.BooleanProperty;
import loan.Loan;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.Constants.GSON_INSTANCE;

public class LoansListRefresher extends TimerTask {

    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<Loan>> loansListConsumer;
    private final BooleanProperty shouldUpdate;


    public LoansListRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<List<Loan>> loansListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.loansListConsumer = loansListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }
        HttpClientUtil.runAsync(Constants.LOANS_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept("Users Request | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                response.body().close();
                httpRequestLoggerConsumer.accept("Users Request | Response: " + jsonArrayOfUsersNames);
                Loan[] allLoans = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, Loan[].class);
                loansListConsumer.accept(Arrays.asList(allLoans));
            }
        });
    }
}
