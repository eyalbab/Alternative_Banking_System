package components.scramble;

import javafx.concurrent.Task;
import loan.Loan;

import java.util.List;

public class TaskFindLoans extends Task<List<Loan>> {
    private ScrambleController controller;
    private List<Loan> loans;

    public TaskFindLoans(ScrambleController controller, List<Loan> loans) {
        this.controller = controller;
        this.loans = loans;
    }

    @Override
    protected List<Loan> call() throws Exception {
        updateProgress(0, 500);
        updateMessage("Searching for loans");
        Thread.sleep(500);
        updateProgress(200, 500);
        Thread.sleep(500);
        updateProgress(300, 500);
        Thread.sleep(500);
        updateProgress(400, 500);
        Thread.sleep(500);
        updateProgress(500, 500);
        updateMessage("Done");
        controller.addFilteredLoanDetails(loans);
        return null;
    }
}
