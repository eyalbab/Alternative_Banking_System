package loan;

public class OfferedLoan {
    private Loan loan;
    private String offererName;

    public OfferedLoan(Loan loan, String custOffered) {
        this.loan = loan;
        this.offererName = custOffered;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public String getOffererName() {
        return offererName;
    }
}
