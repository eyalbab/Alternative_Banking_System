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


@WebServlet(name = "NewLoanServlet", urlPatterns = {"/newLoan"})
public class NewLoanServlet extends HttpServlet {
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
            String loanName = request.getParameter("loanName");
            String capital = request.getParameter("capital");
            String totalYaz = request.getParameter("totalYaz");
            String payRate = request.getParameter("payRate");
            String interest = request.getParameter("interest");
            String category = request.getParameter("category");
            try {
                Loan toAdd = engine.createNewLoan(loanName, userName, category, capital, totalYaz, payRate, interest);
                engine.addLoan(toAdd, customer.get());
                response.setStatus(HttpServletResponse.SC_OK);
                ServletUtils.setMessageOnResponse(response.getWriter(), "New loan created");
            } catch (AbsException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), e.getErrorMsg());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletUtils.setMessageOnResponse(response.getWriter(), "Something went wrong");
        }
    }
}