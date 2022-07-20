package loan;

import utils.AbsException;
import jaxb.generated.AbsLoan;
import jaxb.generated.AbsLoans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Loans implements Serializable {
    private List<Loan> allLoans;

    public Loans(){
        allLoans = new ArrayList<Loan>();
    }

    public Loans(List<Loan> allLoans) {
        this.allLoans = allLoans;
    }

    public static Loans ConvertRawAbsToLoans(AbsLoans rawVer, String owner) throws AbsException {
        List<Loan> resList = new ArrayList<>();
        for (AbsLoan loan : rawVer.getAbsLoan()) {
            Loan toAdd = Loan.ConvertRawAbsToLoan(loan, owner);
            if (!resList.isEmpty()) {
                for (Loan loanTmp : resList) {
                    if (loanTmp.getId().equals(toAdd.getId())) {
                        throw new AbsException("We can't have two loans with same ID");
                    }
                }
            }
            resList.add(toAdd);
        }
        return new Loans(resList);
    }

    public List<Loan> getAllLoans() {
        return allLoans;
    }

    public Optional<Loan> getLoanById(String name) {
        for (Loan loan : allLoans) {
            if (loan.getId().equals(name)) {
                return Optional.of(loan);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Loans in the system:\n----------------------\n");
        int i = 1;
        for (Loan loan : this.getAllLoans()) {
            res.append(i++).append(". ").append(loan);
        }
        return res.toString();
    }
}
