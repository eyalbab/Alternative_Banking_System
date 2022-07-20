package loan;

import java.io.Serializable;

public class Payment implements Serializable {

    private final Integer yazOfPay;
    private final Double capital;
    private final Double interest;
    private final Boolean paid;

    public Payment(Integer yazOfPay, Double capital, Double interest, Boolean paid) {
        this.yazOfPay = yazOfPay;
        this.capital = capital;
        this.interest = interest;
        this.paid = paid;
    }

    public Integer getYazOfPay() {
        return yazOfPay;
    }

    public Double getCapital() {
        return capital;
    }

    public Double getInterest() {
        return interest;
    }

    public Boolean getPaid() {
        return paid;
    }

    @Override
    public String toString() {
        String res = "Yaz of pay: " + yazOfPay +
                ", capital: " + capital +
                ", interest: " + interest +
                ", total pay: " + (capital + interest) +
                '\n';
        if(!getPaid()){
            res = "# DIDN'T PAY! "+res;
        }
        return res;
    }
}
