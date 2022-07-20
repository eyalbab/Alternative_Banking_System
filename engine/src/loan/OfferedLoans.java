package loan;

import java.util.ArrayList;
import java.util.List;

public class OfferedLoans {
    private List<OfferedLoan> offeredLoans;

    public OfferedLoans() {
        offeredLoans = new ArrayList<>();
    }

    public OfferedLoans(List<OfferedLoan> offeredLoans) {
        this.offeredLoans = offeredLoans;
    }

    public List<OfferedLoan> getOfferedLoans() {
        return offeredLoans;
    }

    public Boolean addOfferedLoan(OfferedLoan ol) {
        for (OfferedLoan loan : offeredLoans) {
            if (loan.getLoan().getId().equals(ol.getLoan().getId())) {
                return false;
            }
        }
        offeredLoans.add(ol);
        return true;
    }

    public List<Loan> getLoansOnly() {
        List<Loan> res = new ArrayList<>();
        for (OfferedLoan ol : offeredLoans) {
            res.add(ol.getLoan());
        }
        return res;
    }

    public String getOffererByLoanId(String id) {
        for (OfferedLoan ol : offeredLoans) {
            if (ol.getLoan().getId().equals(id)) {
                return ol.getOffererName();
            }
        }
        return null;
    }

    public void removeFromOffered(String loanName) {
        OfferedLoan toRem = null;
        for (OfferedLoan ol : offeredLoans) {
            if (ol.getLoan().getId().equals(loanName)) {
                toRem = ol;
            }
        }
        if (toRem != null)
            offeredLoans.remove(toRem);
    }
}
