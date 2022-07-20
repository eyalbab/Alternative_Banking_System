package servlets.customer;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Optional;

import static constants.Constants.USERNAME;


@WebServlet(name = "DepositServlet", urlPatterns = {"/deposit"})
public class DepositServlet extends HttpServlet {
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
            Boolean validDeposit = engine.handleDeposit(customer.get(), amount);
            if (validDeposit) {
                response.setStatus(HttpServletResponse.SC_OK);
                ServletUtils.setMessageOnResponse(response.getWriter(), "Deposit was successful");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), "Invalid sum to deposit");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletUtils.setMessageOnResponse(response.getWriter(), "Something went wrong");
        }
    }
}