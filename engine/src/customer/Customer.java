package customer;

import utils.AbsException;
import loan.Loan;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Customer implements Serializable {
    private final String name;
    private Double balance;
    private List<Transaction> transactions;
    private List<Loan> borrowLoans;
    private List<Loan> lendingLoans;
    private Integer numOfNewBorrowed;
    private Integer numOfPendingBorrowed;
    private Integer numOfActiveBorrowed;
    private Integer numOfRiskBorrowed;
    private Integer numOfFinishedBorrowed;
    private Integer numOfNewLended;
    private Integer numOfPendingLended;
    private Integer numOfActiveLended;
    private Integer numOfRiskLended;
    private Integer numOfFinishedLended;
    private List<String> notifications = new ArrayList<>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Loan> getBorrowLoans() {
        return borrowLoans;
    }

    public List<Loan> getLendingLoans() {
        return lendingLoans;
    }

    public Integer getNumOfNewBorrowed() {
        return Math.toIntExact(borrowLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.NEW)).count());
    }

    public Integer getNumOfPendingBorrowed() {
        return Math.toIntExact(borrowLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.PENDING)).count());
    }

    public Integer getNumOfActiveBorrowed() {
        return Math.toIntExact(borrowLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.ACTIVE)).count());
    }

    public Integer getNumOfRiskBorrowed() {
        return Math.toIntExact(borrowLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.RISK)).count());
    }

    public Integer getNumOfFinishedBorrowed() {
        return Math.toIntExact(borrowLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.FINISHED)).count());
    }

    public Integer getNumOfNewLended() {
        return Math.toIntExact(lendingLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.NEW)).count());
    }

    public Integer getNumOfPendingLended() {
        return Math.toIntExact(lendingLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.PENDING)).count());
    }

    public Integer getNumOfActiveLended() {
        return Math.toIntExact(lendingLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.ACTIVE)).count());
    }

    public Integer getNumOfRiskLended() {
        return Math.toIntExact(lendingLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.RISK)).count());
    }

    public Integer getNumOfFinishedLended() {
        return Math.toIntExact(lendingLoans.stream().filter(loan -> loan.getStatus().equals(Loan.LoanStatus.FINISHED)).count());
    }

    public List<Loan> getPaymentActiveLoans() {
        List<Loan.LoanStatus> paymentActive = Arrays.asList(Loan.LoanStatus.ACTIVE, Loan.LoanStatus.RISK);
        return borrowLoans.stream().filter(loan -> paymentActive.contains(loan.getStatus())).collect(Collectors.toList());
    }

    public List<Loan> getLendingPaymentActiveLoans() {
        List<Loan.LoanStatus> paymentActive = Arrays.asList(Loan.LoanStatus.ACTIVE);
        return lendingLoans.stream().filter(loan -> paymentActive.contains(loan.getStatus())).collect(Collectors.toList());
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public Customer(String name) {
        this.name = name;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.borrowLoans = new ArrayList<>();
        this.lendingLoans = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Transactions";
//        StringBuilder res = new StringBuilder("*** " + name + " ***\n" + "Transactions:\n");
//        for (Transaction tran : transactions) {
//            res.append(tran.toString()).append("\n");
//        }
//        res.append(customerLoansToString(Boolean.TRUE));
//        res.append(customerLoansToString(Boolean.FALSE));
//        return res.toString();
    }

    public String customerLoansToString(Boolean isBorrowLoans) {
        StringBuilder res = new StringBuilder(isBorrowLoans ? "Borrow loans: \n" : "Lending loans: \n");
        List<Loan> toIter = isBorrowLoans ? borrowLoans : lendingLoans;
        for (Loan loan : toIter) {
            res.append(loanInfoForCustomer(loan));
        }

        return res.toString();

    }

    public static String loanInfoForCustomer(Loan loan) {
        String res =
                "name: " + loan.getId() + "\n" +
                        "status: " + loan.getStatus().toString().toLowerCase() + "\n" +
                        "category: " + loan.getCategory() + "\n" +
                        "capital: " + loan.getCapital() + "\n" +
                        "rate: " + loan.getPaymentRatio() + "\n" +
                        "interest: " + loan.getInterest() + "\n" +
                        "total sum: " + loan.getTotalPay() + "\n";
        switch (loan.getStatus()) {
            case NEW:
                break;
            case PENDING:
                res += "Left for active: " + (loan.getCapital() - loan.getRecruited()) + "\n";
                break;
            case ACTIVE:
                res += "Next payment yaz: " + loan.getNextPaymentYaz() + ", next payment sum: "
                        + (loan.getTotalPay() / loan.getTotalPayCount()) + "\n";
                break;
            case RISK:
                res = loan.getRiskedPaymentInfo(res);
                break;
            case FINISHED:
                res += "Activated yaz: " + loan.getActivatedYaz() + ", finished yaz: " +
                        loan.getFinishedYaz() + "\n";
                break;
        }
        res += "\n";
        return res;
    }

    public void addTransaction(int yaz, Double sum, Boolean income) {
        if (income) {
            transactions.add(new Transaction(yaz, sum, Boolean.TRUE, getBalance()));
            setBalance(getBalance() + sum);
        } else {
            transactions.add(new Transaction(yaz, sum, Boolean.FALSE, getBalance()));
            setBalance(getBalance() - sum);
        }
    }

    public void addNewLending(Loan addToLending, Double investment, Integer currentYaz) {
        transactions.add(new Transaction(currentYaz, investment, false, balance));
        setBalance(balance - investment);
        if (!lendingLoans.contains(addToLending)) {
            lendingLoans.add(addToLending);
        }
    }

    public void addNewBorrowing(Loan toAdd) {
        borrowLoans.add(toAdd);
    }

//    public Map<Loan, Double> handleContinue(Integer yaz) {
//        Map<Loan, Double> isPaid = new HashMap<>();
//        List<Loan.LoanStatus> activatedStatuses = Arrays.asList(Loan.LoanStatus.ACTIVE, Loan.LoanStatus.RISK);
//        List<Loan> activatedSortedLoans = borrowLoans.stream()
//                .filter(loan -> activatedStatuses.contains(loan.getStatus()))
//                .filter(loan -> loan.getNextPaymentYaz() == yaz)
//                .sorted(Loan::compareTo)
//                .collect(Collectors.toList());
//        for (Loan loan : activatedSortedLoans) {
//            if (loan.getNextPaymentSum() <= balance) {                 //Case of still ACTIVE
//                addTransaction(yaz, loan.getNextPaymentSum(), false);
//                loan.handlePayment(loan.getNextCapitalPaymentSum(), loan.getNextInterestPaymentSum(), yaz, true);
//                isPaid.put(loan, loan.getNextPaymentSum());
//                if (loan.getTotalPaid() >= loan.getTotalPay()) {           //Case of FINISHED
//                    loan.setStatus(Loan.LoanStatus.FINISHED);
//                    loan.setFinishedYaz(yaz);
//                } else {
//                    loan.setStatus(Loan.LoanStatus.ACTIVE);            //case it was RISK and became ACTIVE again
//                    loan.setNextCapitalPaymentSum(loan.getNormalNextCapitalPaySum());
//                    loan.setNextInterestPaymentSum(loan.getNormalNextInterestPaySum());
//                    loan.setNextPaymentSum(loan.getNextPaymentSum());
//                }
//            } else {                                                  //Case of becoming RISK
//                loan.handlePayment(loan.getNextCapitalPaymentSum(), loan.getNextInterestPaymentSum(), yaz, false);
//                loan.setStatus(Loan.LoanStatus.RISK);
//                if (loan.getActivatedYaz() + loan.getTotalYazTime() > yaz) {
//                    loan.setNextCapitalPaymentSum(loan.getNextCapitalPaymentSum() + loan.getNormalNextCapitalPaySum());
//                    loan.setNextInterestPaymentSum(loan.getNextInterestPaymentSum() + loan.getNormalNextInterestPaySum());
//                    loan.setNextPaymentSum(loan.getNextPaymentSum());
//                }
//            }
//            loan.setNextPaymentYaz(loan.getNextPaymentYaz() + loan.getPaymentRatio());
//        }
//        return isPaid;
//    }

    public void handlePayment(Integer yaz, Loan loan) {
        addTransaction(yaz, loan.getNextPaymentSum(), false);
        loan.handlePayment(loan.getNextCapitalPaymentSum(), loan.getNextInterestPaymentSum(), yaz, true);
        if (loan.getTotalPaid() >= loan.getTotalPay()) {           //Case of FINISHED
            loan.setStatus(Loan.LoanStatus.FINISHED);
            loan.setFinishedYaz(yaz);
        } else {
            loan.setStatus(Loan.LoanStatus.ACTIVE);            //case it was RISK and became ACTIVE again or stay ACTIVE
            loan.setNextCapitalPaymentSum(loan.getNormalNextCapitalPaySum());
            loan.setNextInterestPaymentSum(loan.getNormalNextInterestPaySum());
            loan.setNextPaymentSum(loan.getNextPaymentSum());
        }
        loan.setNextPaymentYaz(loan.getNextPaymentYaz() + loan.getPaymentRatio());
    }

    public Double handlePaymentInRisk(Integer yaz, Loan loan, Double amount) {
        if (loan.getNextPaymentSum() <= amount) {                 //Case of become ACTIVE or FINISHED
            if (amount > (loan.getTotalPay() - loan.getTotalPaid())) {
                amount = loan.getTotalPay() - loan.getTotalPaid();
            }
            Double remainder = amount - loan.getNextPaymentSum();
            addTransaction(yaz, amount, false);
            loan.handlePayment(amount - ((amount * loan.getInterest()) / 100), (amount * loan.getInterest()) / 100, yaz, true);
            if (loan.getTotalPaid() >= loan.getTotalPay()) {           //Case of FINISHED
                loan.setStatus(Loan.LoanStatus.FINISHED);
                loan.setFinishedYaz(yaz);
            } else {
                loan.setStatus(Loan.LoanStatus.ACTIVE);            //case it was RISK and became ACTIVE again
                loan.setNextCapitalPaymentSum(loan.getNormalNextCapitalPaySum() - (remainder - (remainder * loan.getInterest() / 100)));
                loan.setNextInterestPaymentSum(loan.getNormalNextInterestPaySum() - (remainder * loan.getInterest() / 100));
                loan.setNextPaymentSum(loan.getNextPaymentSum());
            }
            loan.setNextPaymentYaz(loan.getNextPaymentYaz() + loan.getPaymentRatio());
        } else {                                                  //Case of stay RISK
            Double leftOver = loan.getNextPaymentSum() - amount;
            addTransaction(yaz, amount, false);
            loan.handlePayment(amount - ((amount * loan.getInterest()) / 100), (amount * loan.getInterest()) / 100, yaz, true);
            loan.setNextCapitalPaymentSum(loan.getNormalNextCapitalPaySum() + (leftOver - (leftOver * loan.getInterest() / 100)));
            loan.setNextInterestPaymentSum(loan.getNormalNextInterestPaySum() + (leftOver * loan.getInterest() / 100));
            loan.setNextPaymentSum(loan.getNextPaymentSum());
            if (loan.getActivatedYaz() + loan.getTotalYazTime() > yaz) {
                loan.setNextCapitalPaymentSum(loan.getNextCapitalPaymentSum() + loan.getNormalNextCapitalPaySum());
                loan.setNextInterestPaymentSum(loan.getNextInterestPaymentSum() + loan.getNormalNextInterestPaySum());
                loan.setNextPaymentSum(loan.getNextPaymentSum());
            }
            loan.setNextPaymentYaz(loan.getNextPaymentYaz() + loan.getPaymentRatio());
        }
        return amount;
    }

    public void addNotification(Integer yaz) {
        List<Loan.LoanStatus> activatedStatuses = Arrays.asList(Loan.LoanStatus.ACTIVE, Loan.LoanStatus.RISK);
        List<Loan> activatedSortedLoans = borrowLoans.stream()
                .filter(loan -> activatedStatuses.contains(loan.getStatus()))
                .filter(loan -> loan.getNextPaymentYaz() == yaz)
                .sorted(Loan::compareTo)
                .collect(Collectors.toList());
        for (Loan loan : activatedSortedLoans)
            notifications.add(loan.getId() + " at yaz " + yaz + " had to pay " + loan.getNextPaymentSum());
    }
    public void addNotificationOnLoanOffer(String loanName) {
            notifications.add(loanName + " loan isn't offered no more due to being risked");
    }

    public void removeLending(Loan loanToBuy, Double amount, Integer yaz) {
        lendingLoans.remove(loanToBuy);
        addTransaction(yaz, amount, Boolean.TRUE);
    }
}
