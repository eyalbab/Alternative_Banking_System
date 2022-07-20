package servlets.customer;

import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Loan;
import system.MySystem;
import utils.AbsException;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Optional;


@WebServlet(name = "BuyLoanServlet", urlPatterns = {"/buyLoan"})
public class BuyLoanServlet extends HttpServlet {
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
        Optional<Customer> buyingCustomer = engine.getAllCustomers().getCustomerByName(userName);
        if (buyingCustomer.isPresent()) {
            response.setContentType("application/json");
            String loanName = request.getParameter("loanName");
            try {
                engine.handleBuyLoan(buyingCustomer.get(), loanName);
                response.setStatus(HttpServletResponse.SC_OK);
                ServletUtils.setMessageOnResponse(response.getWriter(), "Bought successfully");
            } catch (AbsException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), e.getErrorMsg());
            }
        }
    }
}