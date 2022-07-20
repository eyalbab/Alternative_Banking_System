package customer;

public class CustomerInfo {
    private Customer customer;
    private Integer yaz;
    private Boolean isRewind;

    public CustomerInfo(Customer customer, Integer yaz, Boolean isRewind) {
        this.customer = customer;
        this.yaz = yaz;
        this.isRewind = isRewind;
    }

    public Boolean getRewind() {
        return isRewind;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Integer getYaz() {
        return yaz;
    }
}
