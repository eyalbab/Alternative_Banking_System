package servlets.customer;

import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Loan;
import loan.OfferedLoan;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "OfferLoanForSaleServlet", urlPatterns = {"/offerLoanForSale"})
public class OfferLoanForSaleServlet extends HttpServlet {

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
            String loanToOfferName = request.getParameter("loanName");
            Optional<Loan> loanToOffer = engine.getLoans().getLoanById(loanToOfferName);
            if (loanToOffer.isPresent()) {
                Boolean addedLoan = engine.offerLoanForSale(new OfferedLoan(loanToOffer.get(), userName));
                if (addedLoan) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    ServletUtils.setMessageOnResponse(response.getWriter(), "Offered loan successfully");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    ServletUtils.setMessageOnResponse(response.getWriter(), "Loan Already for sale");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), "something went wrong");
            }

        }
    }
}