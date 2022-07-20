package servlets.customer;

import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Loan;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Optional;


@WebServlet(name = "PaymentServlet", urlPatterns = {"/payment"})
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = SessionUtils.getUsername(request);
        //Session doesn't exist!
        if (userName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletUtils.setMessageOnResponse(response.getWriter(), "No Session");
            return;
        }
        MySystem engine = ServletUtils.getEngine(getServletContext());
        Optional<Customer> customer = engine.getAllCustomers().getCustomerByName(userName);
        if (customer.isPresent()) {
            response.setContentType("application/json");
            String amount = request.getParameter("amount");
            String loanName = request.getParameter("loanName");
            Boolean close = Boolean.valueOf(request.getParameter("close"));
            if (customer.get().getBalance() >= Double.parseDouble(amount)) {
                Optional<Loan> toPay = engine.getLoans().getLoanById(loanName);
                if (toPay.isPresent()) {
                    engine.handlePayment(customer.get(), toPay.get(), Double.parseDouble(amount), close);
                    response.setStatus(HttpServletResponse.SC_OK);
                    Double newBalance = engine.getAllCustomers().getCustomerByName(userName).get().getBalance();
                    ServletUtils.setMessageOnResponse(response.getWriter(), newBalance.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    ServletUtils.setMessageOnResponse(response.getWriter(), "something wrong with loan");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), "Balance is not enough");
            }
        }
    }
}