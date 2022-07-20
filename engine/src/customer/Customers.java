package customer;

import utils.AbsException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Customers implements Serializable {

    private List<Customer> allCustomers;

    public Customers() {
        allCustomers = new ArrayList<Customer>();
    }

    public List<Customer> getAllCustomers() {
        return allCustomers;
    }

    public Optional<Customer> getCustomerByName(String name) {
        for (Customer customer : allCustomers) {
            if (customer.getName().equals(name)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Customers in the system:\n");
        int i = 1;
        for (Customer cust : this.getAllCustomers()) {
            res.append(i++).append(". ").append(cust).append("\n");
        }
        return res.toString();
    }

    public String showAllCustomersNameByOrder() {
        StringBuilder res = new StringBuilder("Customers in the system:\n");
        int i = 1;
        for (Customer cust : this.getAllCustomers()) {
            res.append(i++).append(". ").append(cust.getName()).append(" Balance - ").append(cust.getBalance()).append("\n");
        }
        return res.toString();
    }

    public void addCustomer(String username) {
        allCustomers.add(new Customer(username));
    }
}
