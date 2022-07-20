package system;

import com.google.gson.Gson;
import customer.Customers;
import loan.Categories;
import loan.Loans;
import loan.OfferedLoans;

public class EngineState {

    private Categories categories;
    private Loans loans;
    private Customers allCustomers;
    private OfferedLoans offeredLoans;

    public EngineState( Categories categories, Loans loans, Customers allCustomers, OfferedLoans ol) {
        this.categories = categories;
        this.loans = loans;
        this.allCustomers = allCustomers;
        this.offeredLoans = ol;
    }

    public OfferedLoans getOfferedLoans() {
        return offeredLoans;
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
}
