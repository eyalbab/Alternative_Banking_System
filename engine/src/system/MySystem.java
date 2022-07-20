package system;

import com.google.gson.Gson;
import customer.Customer;
import customer.Customers;
import loan.*;
import utils.AbsException;
import javafx.collections.ObservableList;
import jaxb.generated.AbsDescriptor;
import utils.ABSUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySystem implements Serializable {

    private String filePath;
    private ObservableList<String> viewByOptions;

    private static final String JAXB_MY_SYSTEM_PACKAGE = "system";
    private Integer yaz = 1;
    private Categories categories;
    private Loans loans;
    private Customers allCustomers;
    private final Boolean isLoaded;
    private Boolean adminLogged;
    private Boolean isInRewind;
    private OfferedLoans loansForSale;
    private Map<Integer, String> states;

    public MySystem() {
        isLoaded = false;
        loans = new Loans();
        categories = new Categories();
        allCustomers = new Customers();
        adminLogged = false;
        loansForSale = new OfferedLoans();
        states = new HashMap<>();
        isInRewind = false;
    }

    public MySystem(Categories categories, Loans loans, Customers allCustomers) {
        this.isLoaded = true;
        this.categories = categories;
        this.loans = loans;
        this.allCustomers = allCustomers;
        loansForSale = new OfferedLoans();
        states = new HashMap<>();
        isInRewind = false;
    }

    public void ConvertDescriptorToSystem(AbsDescriptor rawDescriptor, String username) throws AbsException {
        Categories tmpAllCategories = Categories.ConvertRawAbsToCategories(rawDescriptor.getAbsCategories());
        Loans tmpAllLoans = Loans.ConvertRawAbsToLoans(rawDescriptor.getAbsLoans(), username);
        for (Loan loan : tmpAllLoans.getAllLoans()) {
            if (!tmpAllCategories.getAllCategories().contains(loan.getCategory())) {
                throw new AbsException("Loan " + loan.getCategory() + " category doesn't exist");
            }
            for (Loan loanAlreadyInLoans : loans.getAllLoans()) {
                if (loanAlreadyInLoans.getId().equals(loan.getId()))
                    throw new AbsException("Loan Id already exist");
            }
        }
        for (Loan loan : tmpAllLoans.getAllLoans()) {
            allCustomers.getCustomerByName(username).get().addNewBorrowing(loan);
            loans.getAllLoans().add(loan);
        }
        for (String categ : tmpAllCategories.getAllCategories()) {
            if (!categories.getAllCategories().contains(categ)) {
                categories.getAllCategories().add(categ);
            }
        }
    }

    public void loadFile(InputStream iStream, String username) throws JAXBException, AbsException, FileNotFoundException {
//        InputStream fileStream = new FileInputStream(file);
        AbsDescriptor rawDescriptor = ABSUtils.deserializeFrom(iStream);
        ConvertDescriptorToSystem(rawDescriptor, username);
    }

    public Boolean handleDeposit(Customer cust, String sum) {
        Double sanitizedSum = (double) ABSUtils.tryParseIntAndValidateRange(sum, 0, Integer.MAX_VALUE);
        if (sanitizedSum < 1) {
            return false;
        }
        cust.addTransaction(getYaz(), sanitizedSum, Boolean.TRUE);
        return true;
    }

    public Boolean handleWithdraw(Customer customer, String sum) {
        Double sanitizedSum = ABSUtils.tryParseDoubleAndValidateRange(sum, 0.0, customer.getBalance());
        if (sanitizedSum < 1) {
            return false;
        } else {
            customer.addTransaction(getYaz(), sanitizedSum, Boolean.FALSE);
            return true;
        }

    }

    public Boolean isAdminLogged() {
        return adminLogged;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ObservableList<String> getViewByOptions() {
        return viewByOptions;
    }

    public void setViewByOptions(ObservableList<String> viewByOptions) {
        this.viewByOptions = viewByOptions;
    }

    public Integer getYaz() {
        return yaz;
    }

    public Categories getCategories() {
        return categories;
    }

    public Loans getLoans() {
        return loans;
    }

    public Customers getAllCustomers() {
        return allCustomers;
    }

    public Boolean getLoaded() {
        return isLoaded;
    }


    public String handleLoanAssignmentPicks(Customer customer, String sumToInvest,
                                            String minInterest, String minYazTime,
                                            String maxOwnerLoans) {
        if (ABSUtils.tryParseIntAndValidateRange
                (sumToInvest, 1, customer.getBalance().intValue()) == -1) {
            return "invalid investment sum";
        }
        if (!minInterest.equals("")) {
            if (ABSUtils.tryParseIntAndValidateRange(minInterest, 1, Integer.MAX_VALUE) == -1) {
                return "invalid Interest Percent";
            }
        }
        if (!minYazTime.equals("")) {
            if (ABSUtils.tryParseIntAndValidateRange(minYazTime, 1, Integer.MAX_VALUE) == -1) {
                return "invalid  total yaz time";
            }
        }
        if (!maxOwnerLoans.equals("")) {
            if (ABSUtils.tryParseIntAndValidateRange(maxOwnerLoans, 1, Integer.MAX_VALUE) == -1) {
                return "invalid max owner loans";
            }
        }
        return null;
    }

    public List<Loan> showSuggestedLoans(Customer customer, String sumToInvest, List<String> pickedCategories,
                                         String minInterest, String minYazTime,
                                         String maxOwnerLoans) throws AbsException {
        String picksCheck = handleLoanAssignmentPicks(customer, sumToInvest, minInterest, minYazTime, maxOwnerLoans);
        if (picksCheck == null) {
            if (minInterest.equals("")) minInterest = "0";
            Integer finalInterest = Integer.parseInt(minInterest);
            if (minYazTime.equals("")) minYazTime = "0";
            Integer finalYazTime = Integer.parseInt(minYazTime);
            if (maxOwnerLoans.equals("")) maxOwnerLoans = "100000";
            Integer finalMaxOwnerLoans = Integer.parseInt(maxOwnerLoans);
            List<Loan.LoanStatus> investableStatuses = Arrays.asList(Loan.LoanStatus.NEW, Loan.LoanStatus.PENDING);
            return loans.getAllLoans().stream()
                    .filter(loan -> !loan.getOwner().equals(customer.getName()))
                    .filter(loan -> pickedCategories.contains(loan.getCategory()))
                    .filter(loan -> investableStatuses.contains(loan.getStatus()))
                    .filter(loan -> loan.getInterest() >= finalInterest)
                    .filter(loan -> loan.getTotalYazTime() >= finalYazTime)
                    .filter(loan -> {
                        long count = getAllCustomers().getCustomerByName(loan.getOwner()).get()
                                .getBorrowLoans().stream().filter(borrowedLoan ->
                                        !borrowedLoan.getStatus().equals(Loan.LoanStatus.FINISHED)).count();
                        if (count <= finalMaxOwnerLoans) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        } else {
            throw new AbsException(picksCheck);
        }
    }

    private List<String> validateCategoriesPick(String categoriesPick) {
        if ("".equals(categoriesPick)) {
            return getCategories().getAllCategories();
        }
        Integer categSize = getCategories().getAllCategories().size();
        List<String> res = new ArrayList<>();
        String[] tmp = categoriesPick.split(",");
        for (String categ : tmp) {
            Integer toAddIndex = ABSUtils.tryParseIntAndValidateRange(categ, 1, categSize);
            if (toAddIndex.equals(-1)) {
                return null;
            } else {
                res.add(getCategories().getAllCategories().get(toAddIndex - 1));
            }
        }
        return res;
    }

    public List<Loan> handleLoansPick(String loansPick, List<Loan> optionalLoans) {
        if ("".equals(loansPick)) {
            return null;
        }
        List<Loan> res = new ArrayList<>();
        String[] tmp = loansPick.split(",");
        for (String loanPick : tmp) {
            Integer toAddIndex = ABSUtils.tryParseIntAndValidateRange(loanPick, 1, optionalLoans.size());
            if (toAddIndex == -1) {
                return null;
            } else {
                res.add(optionalLoans.get(toAddIndex - 1));
            }
        }
        return res;
    }

    public void assignLoans(Customer assignTo, List<Loan> loansToAssign, Integer sumToInvest) {
        List<Integer> sumToInvestEachLoan = assignmentsAlgorithm(loansToAssign, sumToInvest);
        for (int i = 0; i < loansToAssign.size(); i++) {
            Loan loanToAssign = loansToAssign.get(i);
            Integer sumToInvestPerLoan = sumToInvestEachLoan.get(i);
            loanToAssign.newInvestment(assignTo.getName(), sumToInvestPerLoan, yaz);
            assignTo.addNewLending(loanToAssign, (double) sumToInvestPerLoan, yaz);
        }

    }

    private List<Integer> assignmentsAlgorithm(List<Loan> loansToAssign, Integer sumToInvest) {
        if (loansToAssign == null) return null;
        List<Integer> leftToInvest = new ArrayList<>();
        List<Integer> sumToInvestEachLoan = new ArrayList<>(Collections.nCopies(loansToAssign.size(), 0));
        loansToAssign.forEach(loan -> leftToInvest.add(loan.getCapital() - loan.getRecruited()));
        while (sumToInvest > 0 && countLeftToInvest(leftToInvest) > 0) {
            Integer minIndex = getCurrentMinToInvest(leftToInvest);
            Integer minSumToInvest = leftToInvest.get(minIndex);
            Integer equalSplit = sumToInvest / countLeftToInvest(leftToInvest);
            if (sumToInvest < countLeftToInvest(leftToInvest)) {            //it can't split between all loans
                int i = 0;
                while (sumToInvest > 0 && countLeftToInvest(leftToInvest) > 0) {
                    if (leftToInvest.get(i) > 0) {
                        sumToInvestEachLoan.set(i, sumToInvestEachLoan.get(i) + 1);
                        leftToInvest.set(i, leftToInvest.get(i) - 1);
                        sumToInvest -= 1;
                    }
                    i++;
                }
            } else {
                sumToInvest = (equalSplit <= minSumToInvest) ?
                        splitAmountForAllLoans(sumToInvest, leftToInvest, sumToInvestEachLoan, equalSplit) :
                        splitAmountForAllLoans(sumToInvest, leftToInvest, sumToInvestEachLoan, minSumToInvest);
            }
        }
        return sumToInvestEachLoan;
    }

    private Integer splitAmountForAllLoans(Integer sumToInvest, List<Integer> leftToInvest,
                                           List<Integer> sumToInvestEachLoan, Integer amountToSplit) {
        for (int i = 0; i < leftToInvest.size(); i++) {
            if (leftToInvest.get(i) > 0) {
                sumToInvestEachLoan.set(i, sumToInvestEachLoan.get(i) + amountToSplit);
                leftToInvest.set(i, leftToInvest.get(i) - amountToSplit);
                sumToInvest -= amountToSplit;
            }
        }
        return sumToInvest;
    }

    private Integer countLeftToInvest(List<Integer> leftToInvest) {
        Integer count = 0;
        for (Integer left : leftToInvest) {
            if (left != 0)
                count++;
        }
        return count;
    }

    private Integer getCurrentMinToInvest(List<Integer> leftToInvest) {
        int minIndex = 0;
        while (leftToInvest.get(minIndex).equals(0)) {
            minIndex++;
        }
        for (int i = 1; i < leftToInvest.size(); i++) {
            if (leftToInvest.get(i) < leftToInvest.get(minIndex) && !leftToInvest.get(i).equals(0)) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public void continueTimeline() {
//        addNewState(yaz);
        List<Loan.LoanStatus> activatedStatuses = Arrays.asList(Loan.LoanStatus.ACTIVE, Loan.LoanStatus.RISK); //checks if customer didn't pay at last yaz
        for (Customer customer : allCustomers.getAllCustomers()) {
            List<Loan> activatedSortedLoans = customer.getBorrowLoans().stream()
                    .filter(loan -> activatedStatuses.contains(loan.getStatus()))
                    .filter(loan -> loan.getNextPaymentYaz() == yaz)
                    .sorted(Loan::compareTo)
                    .collect(Collectors.toList());
            for (Loan loan : activatedSortedLoans) {
                Integer numberOfPayments = loan.getLoanPayments().size();
                if (numberOfPayments.equals(0)) {                 ///customer didn't pay last payment
                    setLoanToRisk(loan);
                    String offererName = loansForSale.getOffererByLoanId(loan.getId());
                    if (offererName != null) {
                        loansForSale.removeFromOffered(loan.getId());
                        Customer offerer = allCustomers.getCustomerByName(offererName).get();
                        offerer.addNotificationOnLoanOffer(loan.getId());
                    }
                } else {
                    Payment lastPayment = loan.getLoanPayments().get(numberOfPayments - 1);
                    if (!lastPayment.getYazOfPay().equals(yaz)) {     ///customer didn't pay last payment
                        setLoanToRisk(loan);
                        String offererName = loansForSale.getOffererByLoanId(loan.getId());
                        if (offererName != null) {
                            loansForSale.removeFromOffered(loan.getId());
                            Customer offerer = allCustomers.getCustomerByName(offererName).get();
                            offerer.addNotificationOnLoanOffer(loan.getId());
                        }
                    }
                }
            }
        }
        yaz++;
        for (Customer customer : allCustomers.getAllCustomers()) {
            customer.addNotification(yaz);
        }
    }

    private void setLoanToRisk(Loan loan) {
        loan.handlePayment(loan.getNextCapitalPaymentSum(), loan.getNextInterestPaymentSum(), yaz, false);
        loan.setStatus(Loan.LoanStatus.RISK);
        if (loan.getActivatedYaz() + loan.getTotalYazTime() > yaz) {
            loan.setNextCapitalPaymentSum(loan.getNextCapitalPaymentSum() + loan.getNormalNextCapitalPaySum());
            loan.setNextInterestPaymentSum(loan.getNextInterestPaymentSum() + loan.getNormalNextInterestPaySum());
            loan.setNextPaymentSum(loan.getNextPaymentSum());
        }
        loan.setNextPaymentYaz(loan.getNextPaymentYaz() + loan.getPaymentRatio());
    }

    public void handlePayment(Customer customer, Loan loan, Double amount, Boolean close) {
        if (loan.getStatus().equals(Loan.LoanStatus.ACTIVE) && !close) {
            customer.handlePayment(yaz, loan);
            Set<String> lendersToPay = loan.getLenders().keySet();
            for (String customerName : lendersToPay) {
                Double percentToPay = loan.getLoanPercentForEachLender().get(customerName);
                allCustomers.getCustomerByName(customerName).get()
                        .addTransaction(yaz, (percentToPay * loan.getNextPaymentSum()) / 100.0, true);
            }
        } else {          //status is risk or close loan
            Double paid = customer.handlePaymentInRisk(yaz, loan, amount);
            Set<String> lendersToPay = loan.getLenders().keySet();
            for (String customerName : lendersToPay) {
                Double percentToPay = loan.getLoanPercentForEachLender().get(customerName);
                allCustomers.getCustomerByName(customerName).get()
                        .addTransaction(yaz, (percentToPay * paid) / 100.0, true);
            }
        }
    }

    public boolean isUserExists(String username) {
        return allCustomers.getCustomerByName(username).isPresent();
    }

    public synchronized void addUser(String username) {
        allCustomers.addCustomer(username);
    }

    public void setAdminLogged() {
        this.adminLogged = Boolean.TRUE;
    }

    public synchronized void addLoan(Loan toAdd, Customer cust) throws AbsException {
        for (Loan loan : loans.getAllLoans()) {
            if (loan.getId().equals(toAdd.getId()))
                throw new AbsException("Loan Id already exist");
        }
        loans.getAllLoans().add(toAdd);
        cust.addNewBorrowing(toAdd);
    }

    public Loan createNewLoan(String Id, String owner, String category,
                              String capital, String totalYaz, String payRate, String interest) throws AbsException {
        Integer intCapital = ABSUtils.tryParseIntAndValidateRange(capital, 0, Integer.MAX_VALUE);
        Integer intTotalYaz = ABSUtils.tryParseIntAndValidateRange(totalYaz, 0, Integer.MAX_VALUE);
        Integer intPayRate = ABSUtils.tryParseIntAndValidateRange(payRate, 0, intTotalYaz);
        Integer intInterest = ABSUtils.tryParseIntAndValidateRange(interest, 1, 100);
        if (intCapital == -1)
            throw new AbsException("capital input is invalid");
        if (intTotalYaz == -1)
            throw new AbsException("total yaz input is invalid");
        if (intPayRate == -1)
            throw new AbsException("pay rate input is invalid");
        if (intInterest == -1)
            throw new AbsException("interest input is invalid");
        if (intTotalYaz % intPayRate != 0)
            throw new AbsException("total time doesn't divide with rate");

        Loan res = new Loan(Id, owner, category, intCapital, intTotalYaz, intPayRate, intInterest);
        return res;
    }

    public Boolean offerLoanForSale(OfferedLoan loanToOffer) {
        return loansForSale.addOfferedLoan(loanToOffer);
    }

    public OfferedLoans getLoansForSale(String userName) {
        List<OfferedLoan> resList = new ArrayList<>();
        for (OfferedLoan ol : loansForSale.getOfferedLoans()) {
            if (ol.getOffererName() != userName)
                resList.add(ol);
        }
        return new OfferedLoans(resList);
    }

    public void handleBuyLoan(Customer buyer, String loanName) throws AbsException {
        Optional<Loan> loanToBuyOpt = loans.getLoanById(loanName);
        if (loanToBuyOpt.isPresent()) {
            Loan loanToBuy = loanToBuyOpt.get();
            String offerer = loansForSale.getOffererByLoanId(loanName);
            if (offerer == null) {
                throw new AbsException("couldn't find loan offerer");
            }
            Customer offererCust = getAllCustomers().getCustomerByName(offerer).get();
            Double capitalNeededToPay = loanToBuy.getCapitalForLenderByName(offerer);
            if (buyer.getBalance() < capitalNeededToPay) {
                throw new AbsException("Balance not enough to buy");
            }
            synchronized (this) {
                loansForSale.removeFromOffered(loanName);
                buyer.addNewLending(loanToBuy, capitalNeededToPay, yaz);
                offererCust.removeLending(loanToBuy, capitalNeededToPay, yaz);
                loanToBuy.handleSell(buyer.getName(), offererCust.getName());
            }
        } else {
            throw new AbsException("couldn't find loan in system");
        }
    }

    public void addNewState(Integer yazToSave) {
        EngineState toAdd = new EngineState(categories, loans, allCustomers, loansForSale);
        Gson gson = new Gson();
        states.put(yazToSave, gson.toJson(toAdd));
        System.out.println("yaz saved: " + yazToSave + " data: " + gson.toJson(toAdd));
    }

    public void loadYazState(Integer yazToLoad) {
//        if (!isInRewind) {
//            if (!states.keySet().contains(yazToLoad - 1))
//                addNewState(yazToLoad - 1);
//        }
        if (states.keySet().contains(yazToLoad)) {
            String jsonState = states.get(yazToLoad);
            Gson gson = new Gson();
            EngineState toLoad = gson.fromJson(jsonState, EngineState.class);
            System.out.println("loading yaz:" + yazToLoad);
            categories = toLoad.getCategories();
            loans = toLoad.getLoans();
            allCustomers = toLoad.getAllCustomers();
            yaz = yazToLoad;
            loansForSale = toLoad.getOfferedLoans();
            attachLoans();
        }
    }

    private void attachLoans() {
        for (Customer customer : allCustomers.getAllCustomers()) {
            customer.getBorrowLoans().clear();
            customer.getLendingLoans().clear();
        }
        for (Loan loan : loans.getAllLoans()) {
            allCustomers.getCustomerByName(loan.getOwner()).get().getBorrowLoans().add(loan);
            for (String customerName : loan.getLenders().keySet()) {
                allCustomers.getCustomerByName(customerName).get().getLendingLoans().add(loan);
            }
        }

        for (OfferedLoan offeredLoan : loansForSale.getOfferedLoans()) {
            offeredLoan.setLoan(loans.getLoanById(offeredLoan.getLoan().getId()).get());
        }
    }

    public void setRewind(boolean b) {
        isInRewind = b;
    }


    public Boolean getInRewind() {
        return isInRewind;
    }
}
