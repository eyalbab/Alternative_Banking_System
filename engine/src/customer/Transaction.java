package customer;

import java.io.Serializable;

public class Transaction implements Serializable {

    private Integer yazDate;
    private Double sum;
    private Character income;
    private Double prevBalance;
    private Double afterBalance;

    public Transaction(int yazDate, Double sum, Boolean income, Double prevBalance) {
        this.yazDate = yazDate;
        this.sum = sum;
        this.income = income ? '+' : '-';
        this.prevBalance = prevBalance;
        this.afterBalance = income ? sum + prevBalance : prevBalance - sum;
    }

    @Override
    public String toString() {
        String res = income.toString();
        res += " Yaz:" + yazDate + ", Sum:" + sum + ", Previous Balance:" + prevBalance + ", Current Balance:" + afterBalance;
        return res;
    }
}
