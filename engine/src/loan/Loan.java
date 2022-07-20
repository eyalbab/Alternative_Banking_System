package loan;

import utils.AbsException;
import jaxb.generated.AbsLoan;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loan implements Serializable, Comparable {

    private LoanStatus status;
    private final String category;
    private final Integer capital;            //sum of loan, without interest
    private final Integer totalYazTime;
    private final Integer paymentRatio;       //pay every yaz
    private final Integer interest;
    private Map<String, Integer> lenders;     //key is lender name and the value is the amount of money
    private Integer recruited;
    private final String owner;
    private final String Id;
    private Integer activatedYaz;
    private Integer finishedYaz;
    private Integer nextPaymentYaz;
    private Double nextPaymentSum;
    private Double nextCapitalPaymentSum;
    private Double nextInterestPaymentSum;
    private List<Payment> loanPayments;
    private Map<String, Double> loanPercentForEachLender;

    public Loan(String Id, String owner, String category, Integer capital, Integer totalYaz, Integer payRate, Integer interest) {
        this.Id = Id;
        this.status = LoanStatus.NEW;
        this.category = category;
        this.capital = capital;
        this.totalYazTime = totalYaz;
        this.paymentRatio = payRate;
        this.interest = interest;
        this.owner = owner;
        this.recruited = 0;
        this.lenders = new HashMap<>();
        this.activatedYaz = null;
        this.finishedYaz = null;
        this.nextPaymentYaz = Integer.MAX_VALUE;
        this.nextPaymentSum = null;
        this.loanPayments = new ArrayList<>();
        this.loanPercentForEachLender = new HashMap<>();
    }

    public static Loan ConvertRawAbsToLoan(AbsLoan loan, String owner) throws AbsException {
        if (loan.getAbsCapital() < 1) {
            throw new AbsException("Abs capital must be positive");
        }
        if (loan.getAbsIntristPerPayment() < 0) {
            throw new AbsException("Abs interest must be positive");
        }
        if (loan.getAbsTotalYazTime() < 1) {
            throw new AbsException("Abs total yaz must be positive");
        }
        if (loan.getAbsPaysEveryYaz() < 1) {
            throw new AbsException("Abs payment rate must be positive");
        }
        if (loan.getAbsTotalYazTime() % loan.getAbsPaysEveryYaz() != 0) {
            throw new AbsException("total yaz must divide with rate without remainder");
        }
        String sanityID = ABSUtils.sanitizeStr(loan.getId());
        String sanityCategory = ABSUtils.sanitizeStr(loan.getAbsCategory());
        return new Loan(sanityID, owner, sanityCategory, loan.getAbsCapital(), loan.getAbsTotalYazTime(), loan.getAbsPaysEveryYaz(), loan.getAbsIntristPerPayment());
    }


    public enum LoanStatus {
        NEW, PENDING, ACTIVE, RISK, FINISHED
    }

    public String getId() {
        return Id;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public Integer getCapital() {
        return capital;
    }

    public Integer getTotalYazTime() {
        return totalYazTime;
    }

    public List<Payment> getLoanPayments() {
        return loanPayments;
    }

    public Integer getPaymentRatio() {
        return paymentRatio;
    }

    public Integer getInterest() {
        return interest;
    }

    public Map<String, Integer> getLenders() {
        return lenders;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getTotalPayCount() {
        return totalYazTime / paymentRatio;
    }

    public Integer getRecruited() {
        return recruited;
    }

    public void setActivatedYaz(Integer activatedYaz) {
        this.activatedYaz = activatedYaz;
    }

    public Double getNextPaymentSum() {
        return getNextCapitalPaymentSum() + getNextInterestPaymentSum();
    }

    public void setNextPaymentSum(Double nextPaymentSum) {
        this.nextPaymentSum = nextPaymentSum;
    }

    public void setNextPaymentYaz(Integer nextPaymentYaz) {
        this.nextPaymentYaz = nextPaymentYaz;
    }

    public Integer getFinishedYaz() {
        return finishedYaz;
    }

    public Map<String, Double> getLoanPercentForEachLender() {
        return loanPercentForEachLender;
    }

    public Double getTotalPay() {
        return capital * ((100 + interest) / 100.0);
    }

    public Integer getActivatedYaz() {
        return activatedYaz;
    }

    public Integer getNextPaymentYaz() {
        return nextPaymentYaz;
    }

    public Double getNextCapitalPaymentSum() {
        return nextCapitalPaymentSum;
    }

    public void setNextCapitalPaymentSum(Double nextCapitalPaymentSum) {
        this.nextCapitalPaymentSum = nextCapitalPaymentSum;
    }

    public Double getNextInterestPaymentSum() {
        return nextInterestPaymentSum;
    }

    public void setNextInterestPaymentSum(Double nextInterestPaymentSum) {
        this.nextInterestPaymentSum = nextInterestPaymentSum;
    }


    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public void setFinishedYaz(Integer finishedYaz) {
        this.finishedYaz = finishedYaz;
    }

    @Override
    public String toString() {
        String res = "More Details:\n";
//                "ID: " + getId() + "\n" +
//                        "Owner: " + getOwner() + "\n" +
//                        "Category: " + getCategory() + "\n" +
//                        "Capital: " + getCapital() + ", Total period: " + getTotalYazTime() + " Yaz\n" +
//                        "Interest: " + getInterest() + "%, Pay rate: " + getPaymentRatio() + " Yaz\n" +
//                        "Status: " + getStatus() + "\n" + "\n";
        switch (status) {
            case NEW:
                res += "Loan is still new, no further information";
                break;
            case PENDING:
                //Show all lenders and their investment, total raised sum, amount left for active
                res += "Lenders: \n";
                for (Map.Entry<String, Integer> entry : lenders.entrySet()) {
                    res += entry.getKey() + " payed " + entry.getValue() + "\n";
                }
                res += "Amount that has been recruited: " + getRecruited() + "\n" +
                        "Amount that left to recruit: " + (getCapital() - getRecruited()) + "\n\n";
                break;
            case ACTIVE:
                res = getActiveInfo(res);
                break;
            case RISK:
                res = getActiveInfo(res);
                res = getRiskedPaymentInfo(res);
                break;
            case FINISHED:
                res += "Lenders: \n";
                for (Map.Entry<String, Integer> entry : lenders.entrySet()) {
                    res += entry.getKey() + " payed " + entry.getValue() + "\n";
                }
                res += "Active yaz: " + activatedYaz + ", Finished yaz: " + finishedYaz + "\n";
                res += showPaymentInfo(Boolean.TRUE);
                break;
        }
        return res;
    }

    @Override
    public int compareTo(Object o) {
        Loan loan = (Loan) o;
        return this.activatedYaz - loan.activatedYaz;
    }

    private String getActiveInfo(String res) {
        res += "Lenders: \n";
        for (Map.Entry<String, Integer> entry : lenders.entrySet()) {
            res += entry.getKey() + " payed " + entry.getValue() + "\n";
        }
        res += "Activated on: " + getActivatedYaz() + " yaz, Next payment on: " + getNextPaymentYaz() + " Yaz\n";
        res += showPaymentInfo(Boolean.FALSE);
        Double capitalPaid = getCurrentTotalPaidCapital();
        Double interestPaid = getCurrentTotalPaidInterest();
        res += "Paid Capital: " + capitalPaid + " Paid Interest: " + interestPaid + " Left Capital: "
                + (capital - capitalPaid) + " left interest: " + ((getTotalPay() - capital) - interestPaid + "\n\n");
        return res;
    }

    public String getRiskedPaymentInfo(String res) {
        int numOfUnpaid = 0;
        int sumOfUnpaid = 0;
        for (Payment payment : loanPayments
        ) {
            if (!payment.getPaid()) {
                numOfUnpaid++;
                sumOfUnpaid += payment.getCapital() + payment.getInterest();
            }
        }
        res += "Number of unpaid: " + numOfUnpaid + ", total sum unpaid: " + sumOfUnpaid + "\n";
        return res;

    }

    public Double getCurrentTotalPaidCapital() {
        Double res = 0.0;
        for (Payment payment : loanPayments
        ) {
            if (payment.getPaid()) {
                res += payment.getCapital();
            }
        }
        return res;
    }

    public Double getCurrentTotalPaidInterest() {
        Double res = 0.0;
        for (Payment payment : loanPayments
        ) {
            if (payment.getPaid()) {
                res += payment.getInterest();
            }
        }
        return res;
    }

    public Double getTotalPaid() {
        return getCurrentTotalPaidCapital() + getCurrentTotalPaidInterest();
    }

    public String showPaymentInfo(Boolean onlyPaid) {
        String res = "Loan Payment:\n";
        for (Payment payment : loanPayments
        ) {
            if (onlyPaid) {
                if (payment.getPaid()) {
                    res += payment.toString();
                }
            } else {
                res += payment.toString();
            }
        }
        res += "\n";
        return res;
    }

    public Double getNormalNextPaymentSum() {
        return getNormalNextCapitalPaySum() + getNormalNextInterestPaySum();
    }

    public Double getNormalNextCapitalPaySum() {
        return capital / (double) getTotalPayCount();
    }

    public Double getNormalNextInterestPaySum() {
        return getNormalNextCapitalPaySum() * (interest / 100.0);
    }

    public void newInvestment(String investorName, Integer investment, Integer currentYaz) {
        Integer alreadyInvested = lenders.getOrDefault(investorName, 0);
        lenders.put(investorName, alreadyInvested + investment);
        recruited += investment;
        if (status.equals(LoanStatus.NEW)) {
            setStatus(LoanStatus.PENDING);
        }
        if (recruited.equals(capital)) {
            setStatus(LoanStatus.ACTIVE);
            setActivatedYaz(currentYaz);
            nextPaymentYaz = currentYaz + paymentRatio;
            nextCapitalPaymentSum = getNormalNextCapitalPaySum();
            nextInterestPaymentSum = getNormalNextInterestPaySum();
            nextPaymentSum = getNormalNextPaymentSum();
            lenders.keySet().forEach(lender -> loanPercentForEachLender.put(lender, ((double) lenders.get(lender) / capital) * 100.0));
        }
    }

    public Double getCapitalForLenderByName(String lender) {
        double capitalLeftToPay = getCapital() - getCurrentTotalPaidCapital();
        Double percentOfLenderPart = loanPercentForEachLender.get(lender) / 100.0;
        return capitalLeftToPay * percentOfLenderPart;
    }

    public void handlePayment(Double capitalSum, Double interestSum, Integer yaz, Boolean paid) {
        loanPayments.add(new Payment(yaz, capitalSum, interestSum, paid));
    }

    public void handleSell(String buyer, String seller){
        Integer alreadyInvested = lenders.getOrDefault(buyer, 0);
        Integer investment = lenders.get(seller);
        lenders.put(buyer, alreadyInvested + investment);
        lenders.remove(seller);
        Double alreadyPercent = loanPercentForEachLender.getOrDefault(buyer, 0.0);
        Double percent = loanPercentForEachLender.get(seller);
        loanPercentForEachLender.put(buyer, alreadyPercent + percent);
        loanPercentForEachLender.remove(seller);
    }

}
